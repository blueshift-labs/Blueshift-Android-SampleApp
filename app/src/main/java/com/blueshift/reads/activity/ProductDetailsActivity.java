package com.blueshift.reads.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blueshift.Blueshift;
import com.blueshift.inappmessage.InAppManager;
import com.blueshift.reads.R;
import com.blueshift.reads.ShoppingCart;
import com.blueshift.reads.async.GetBookDetailsTask;
import com.blueshift.reads.framework.ReadsBaseActivity;
import com.blueshift.reads.model.Book;
import com.blueshift.rich_push.Message;
import com.blueshift.rich_push.RichPushConstants;
import com.bumptech.glide.Glide;
import com.github.rahulrvp.android_utils.EditTextUtils;
import com.github.rahulrvp.android_utils.TextViewUtils;


public class ProductDetailsActivity extends ReadsBaseActivity {

    public static final String EXTRA_BOOK = "book";
    private EditText mQtyField;
    private Book mBook;
    private ImageView mBookCoverImage;
    private TextView mNameText;
    private TextView mPriceText;
    private TextView mSkuText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Blueshift.getInstance(this).trackScreenView(this, false);

        mBookCoverImage = findViewById(R.id.book_cover);
        mNameText = findViewById(R.id.book_name);
        mPriceText = findViewById(R.id.book_price);
        mSkuText = findViewById(R.id.book_sku);
        mQtyField = findViewById(R.id.book_qty);

        mBook = (Book) getIntent().getSerializableExtra(EXTRA_BOOK);
        if (mBook != null) {
            fillInBookDetails(mBook);
        } else {
            Message message = (Message) getIntent().getSerializableExtra(RichPushConstants.EXTRA_MESSAGE);
            if (message != null) {
                searchAndDisplayBookDetails(message.getProductId());
            } else {
                noDetailsClosePage();
            }
        }
    }

    private void searchAndDisplayBookDetails(String sku) {
        new GetBookDetailsTask(this)
                .setCallback(new GetBookDetailsTask.Callback() {
                    @Override
                    public void onTaskStart() {
                        showProgressDialog(R.string.fetching_details);
                    }

                    @Override
                    public void onTaskComplete(Book book) {
                        hideProgressDialog();

                        if (book != null) {
                            fillInBookDetails(book);
                        } else {
                            noDetailsClosePage();
                        }
                    }
                })
                .setSku(sku)
                .execute();
    }

    private void fillInBookDetails(Book book) {
        if (book != null) {

            // One event sent as batch event for testing purpose.
            Blueshift
                    .getInstance(this)
                    .trackProductView(book.getSku(), 1, true);

            if (mBookCoverImage != null) {
                Glide
                        .with(this)
                        .load(book.getImageUrl())
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .crossFade()
                        .into(mBookCoverImage);
            }

            if (mNameText != null) {
                mNameText.setText(book.getName());
            }

            TextViewUtils.setText(mPriceText, R.string.dollar_x, book.getPrice());

            if (mSkuText != null) {
                mSkuText.setText(book.getSku());
            }

            if (mQtyField != null) {
                String qty = EditTextUtils.getText(mQtyField);
                if (!TextUtils.isEmpty(qty)) {
                    mQtyField.setSelection(qty.length());
                }
            }
        }
    }

    private void noDetailsClosePage() {
        Toast.makeText(this, "No product details found.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        ShoppingCart.getInstance(this).save(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        ShoppingCart cart = ShoppingCart.getInstance(this);

        MenuItem item = menu.findItem(R.id.menu_cart);
        if (item != null) {
            String string = getString(R.string.cart_0, cart.getCount());
            item.setTitle(string);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_cart) {
            Intent intent = new Intent(this, PlaceOrderActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_live) {
            Intent intent = new Intent(this, LiveContentActivity.class);
            startActivity(intent);
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        InAppManager.registerForInAppMessages(this);
        invalidateOptionsMenu();
    }

    @Override
    protected void onStop() {
        InAppManager.unregisterForInAppMessages(this);
        super.onStop();
    }


    private int getQuantityInt(String quantity) {
        int qty = 0;

        if (!TextUtils.isEmpty(quantity)) {
            try {
                qty = Integer.valueOf(quantity);
            } catch (Exception ignored) {
            }
        }

        return qty;
    }

    public void onAddToWishListClick(View view) {
        Toast.makeText(this, "Item added to wish list (No events sent).", Toast.LENGTH_SHORT).show();
    }

    public void onAddToCartClick(View view) {
        int quantity = getQuantityInt(EditTextUtils.getText(mQtyField));

        if (quantity == 0) {
            EditTextUtils.setError(mQtyField, "Invalid quantity. Enter a value >= 1");
        } else {
            if (mBook != null) {
                mBook.setQuantity(quantity);
                ShoppingCart.getInstance(this).add(mBook);

                Blueshift
                        .getInstance(this)
                        .trackAddToCart(mBook.getSku(), quantity, false);

                Toast.makeText(this, "Item added to cart.", Toast.LENGTH_SHORT).show();

                invalidateOptionsMenu();
            }
        }
    }
}
