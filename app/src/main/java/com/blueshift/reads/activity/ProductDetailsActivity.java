package com.blueshift.reads.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.blueshift.BlueshiftAppPreferences;
import com.blueshift.BlueshiftExecutor;
import com.blueshift.inbox.BlueshiftInboxActivity;
import com.blueshift.model.UserInfo;
import com.blueshift.reads.R;
import com.blueshift.reads.ShoppingCart;
import com.blueshift.reads.TestUtils;
import com.blueshift.reads.async.GetBookDetailsTask;
import com.blueshift.reads.framework.ReadsBaseActivity;
import com.blueshift.reads.model.Book;
import com.blueshift.rich_push.Message;
import com.blueshift.rich_push.RichPushConstants;
import com.bumptech.glide.Glide;
import com.github.rahulrvp.android_utils.EditTextUtils;
import com.github.rahulrvp.android_utils.TextViewUtils;
import com.google.gson.Gson;


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
//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Blueshift.getInstance(this).trackScreenView(this, false);

        mBookCoverImage = findViewById(R.id.book_cover);
        mNameText = findViewById(R.id.book_name);
        mPriceText = findViewById(R.id.book_price);
        mSkuText = findViewById(R.id.book_sku);
        mQtyField = findViewById(R.id.book_qty);

        Uri uri = getIntent().getData();
        if (uri != null) {
            deepLink(uri.getPath());
        } else {
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
    protected void onStart() {
        super.onStart();
        Blueshift.getInstance(this).registerForInAppMessages(this, "product_details");

        invalidateOptionsMenu();
    }

    @Override
    protected void onStop() {
        Blueshift.getInstance(this).unregisterForInAppMessages(this);
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

    private void deepLink(final String url) {
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "No URL found", Toast.LENGTH_SHORT).show();
        } else {
            if (url.endsWith("/checkout")) {
                Intent cartIntent = new Intent(mContext, PlaceOrderActivity.class);
                startActivity(cartIntent);

                finish();
            } else {
                BlueshiftExecutor.getInstance().runOnDiskIOThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                Book book = null;

                                try {
                                    String json = TestUtils.readTextFileFromAssets(mContext, "products.json");
                                    if (json != null) {
                                        Book[] books = new Gson().fromJson(json, Book[].class);

                                        for (Book item : books) {
                                            if (item != null && item.hasSamePath(url)) {
                                                book = item;

                                                break;
                                            }
                                        }

                                    }
                                } catch (Exception ignored) {

                                }

                                final Book finalBook = book;
                                BlueshiftExecutor.getInstance().runOnMainThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                fillInBookDetails(finalBook);
                                            }
                                        }
                                );
                            }
                        }
                );
            }
        }
    }
}
