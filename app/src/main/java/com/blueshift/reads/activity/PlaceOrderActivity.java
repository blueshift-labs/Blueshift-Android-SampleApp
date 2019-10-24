package com.blueshift.reads.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blueshift.Blueshift;
import com.blueshift.model.Product;
import com.blueshift.model.UserInfo;
import com.blueshift.reads.BuildConfig;
import com.blueshift.reads.R;
import com.blueshift.reads.ShoppingCart;
import com.blueshift.reads.async.GetBookDetailsTask;
import com.blueshift.reads.framework.ReadsBaseActivity;
import com.blueshift.reads.model.Book;
import com.blueshift.rich_push.Message;
import com.blueshift.rich_push.RichPushConstants;
import com.github.rahulrvp.android_utils.EditTextUtils;
import com.github.rahulrvp.android_utils.TextViewUtils;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaceOrderActivity extends ReadsBaseActivity {

    private ShoppingCart mCart;
    private TextView mTotalNoTax;
    private TextView mTotalWithTax;
    private TextInputLayout mNameTIL;
    private TextInputLayout mEmailTIL;
    private TextInputLayout mCompanyTIL;
    private TextInputLayout mContactTIL;
    private String mName;
    private String mEmail;
    private String mContact;
    private ProgressDialog mProgressDialog;
    private CartProductsAdapter mRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        setTitle(R.string.review_products);

        mTotalNoTax = findViewById(R.id.total_excl_tax);
        mTotalWithTax = findViewById(R.id.total_with_tax);

        mNameTIL = findViewById(R.id.order_name);
        mEmailTIL = findViewById(R.id.order_email);
        mCompanyTIL = findViewById(R.id.order_company);
        mContactTIL = findViewById(R.id.order_contact);

        RecyclerView productRView = findViewById(R.id.cart_product_list);
        productRView.setLayoutManager(new LinearLayoutManager(this));

        mRVAdapter = new CartProductsAdapter();
        productRView.setAdapter(mRVAdapter);

        mCart = ShoppingCart.getInstance(this);

        Message message = (Message) getIntent().getSerializableExtra(RichPushConstants.EXTRA_MESSAGE);
        if (message != null) {
            addToCart(message.getProductId());
        } else {
            loadCartItems();
        }

        UserInfo userInfo = UserInfo.getInstance(this);
        EditTextUtils.setText(mEmailTIL.getEditText(), userInfo.getEmail());

        Blueshift.getInstance(this).trackScreenView(this, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Blueshift.getInstance(this).registerForInAppMessages(this);
        Blueshift.getInstance(this).displayInAppMessages();
    }

    @Override
    protected void onStop() {
        Blueshift.getInstance(this).unregisterForInAppMessages(this);
        super.onStop();
    }

    private void loadCartItems() {
        if (mCart != null && mRVAdapter != null) {
            mRVAdapter.setBooks(mCart.getBooks());
        }

        updateSummaryView();
    }

    private void addToCart(String sku) {
        new GetBookDetailsTask(this)
                .setCallback(new GetBookDetailsTask.Callback() {
                    @Override
                    public void onTaskStart() {
                        showProgressDialog(R.string.add_to_cart);
                    }

                    @Override
                    public void onTaskComplete(Book book) {
                        hideProgressDialog();

                        if (book != null && mCart != null) {
                            mCart.add(book);
                        }

                        loadCartItems();
                    }
                })
                .setSku(sku)
                .execute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        ShoppingCart.getInstance(this).save(this);
    }

    private void updateSummaryView() {
        Float totalAmt = mCart.getTotalAmount();
        String totalAmtStr = String.format(Locale.getDefault(), "%.2f", totalAmt);

        TextViewUtils.setText(mTotalNoTax, R.string.dollar_x, totalAmtStr);
        TextViewUtils.setText(mTotalWithTax, R.string.dollar_x, totalAmtStr);
    }

    public void onPlaceOrderClick(View view) {
        if (hasValidParams()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Checkout Cart");
            builder.setMessage("Place the order?");
            builder.setNegativeButton("No", null);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    placeOrder();
                }
            });
            builder.create().show();
        }
    }

    private void placeOrder() {
        List<Product> productList = new ArrayList<>();
        List<Book> bookList = mCart.getBooks();
        for (Book book : bookList) {
            Product product = new Product();
            product.setSku(book.getSku());
            product.setPrice(Float.valueOf(book.getPrice()));
            product.setQuantity(book.getQuantity());

            productList.add(product);
        }

        Product[] products = new Product[0];
        productList.toArray(products);

        Float totalAmt = mCart.getTotalAmount();

        Blueshift
                .getInstance(this)
                .trackCheckoutCart(products, totalAmt, 0f, null, false);

        // Place order success.
        long orderId = System.currentTimeMillis();

        Blueshift
                .getInstance(this)
                .trackProductsPurchase(String.valueOf(orderId), products, totalAmt, 0f, 0f, null, false);

        mCart.clear();

        mProgressDialog = new ProgressDialog(this);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("Placing order...");
                mProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mProgressDialog.dismiss();

                showSuccessDialog();
            }
        }.execute();
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Order placed successfully.");
        builder.setPositiveButton("Ok", null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
        builder.create().show();
    }

    private boolean hasValidParams() {
        if (mCart.getCount() == 0) {
            Toast.makeText(this, "No products found.", Toast.LENGTH_SHORT).show();
            return false;
        }

        mName = EditTextUtils.getText(mNameTIL.getEditText());
        if (TextUtils.isEmpty(mName)) {
            mNameTIL.setError("Please enter a name");
            return false;
        }

        mEmail = EditTextUtils.getText(mEmailTIL.getEditText());
        if (TextUtils.isEmpty(mEmail)) {
            mEmailTIL.setError("Please enter an email-id");
            return false;
        }

        boolean validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches();
        if (!validEmail) {
            mEmailTIL.setError("Please enter a valid email-id");
            return false;
        }

        mContact = EditTextUtils.getText(mContactTIL.getEditText());
        if (TextUtils.isEmpty(mContact)) {
            mContactTIL.setError("Please enter a contact");
            return false;
        }

        if (!PhoneNumberUtils.isGlobalPhoneNumber(mContact)) {
            mContactTIL.setError("Please enter a valid phone");
            return false;
        }

        return true;
    }

    private class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.ViewHolder> {

        private final List<Book> mBooks = new ArrayList<>();

        void setBooks(List<Book> books) {
            mBooks.addAll(books);

            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_book_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.fillValues(mBooks.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView mNameText;
            private final TextView mPriceText;
            private final TextView mQuantityText;
            private final Button mPlusBtn;
            private final Button mMinusBtn;
            private final ImageButton mDeleteBtn;

            ViewHolder(View itemView) {
                super(itemView);

                mNameText = itemView.findViewById(R.id.cart_item_book_name);
                mPriceText = itemView.findViewById(R.id.cart_item_book_price);
                mQuantityText = itemView.findViewById(R.id.cart_item_book_quantity);

                mPlusBtn = itemView.findViewById(R.id.qty_increase_btn);
                mMinusBtn = itemView.findViewById(R.id.qty_decrease_btn);
                mDeleteBtn = itemView.findViewById(R.id.cart_item_delete_book_btn);
            }

            void fillValues(Book book, final int position) {
                if (book != null) {
                    TextViewUtils.setText(mNameText, book.getName());
                    TextViewUtils.setText(mPriceText, R.string.dollar_x, book.getPrice());
                    TextViewUtils.setText(mQuantityText, String.valueOf(book.getQuantity()));

                    if (mPlusBtn != null) {
                        mPlusBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                increaseQty(position);
                            }
                        });
                    }

                    if (mMinusBtn != null) {
                        mMinusBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                decreaseQty(position);
                            }
                        });
                    }

                    if (mDeleteBtn != null) {
                        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteItem(position);
                            }
                        });
                    }
                }
            }

            private void decreaseQty(int position) {
                Book book = mBooks.get(position);
                if (book.getQuantity() > 1) {
                    int qty = mCart.decreaseQuantity(book);

                    TextViewUtils.setText(mQuantityText, String.valueOf(qty));

                    updateSummaryView();
                }
            }

            private void increaseQty(int position) {
                Book book = mBooks.get(position);

                int qty = mCart.increaseQuantity(book);

                TextViewUtils.setText(mQuantityText, String.valueOf(qty));

                updateSummaryView();
            }

            private void deleteItem(int position) {
                Book book = mBooks.get(position);
                mCart.remove(book);

                mBooks.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mBooks.size());

                updateSummaryView();
            }
        }
    }
}
