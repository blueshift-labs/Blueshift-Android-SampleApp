package com.blueshift.reads.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blueshift.Blueshift;
import com.blueshift.reads.R;
import com.blueshift.reads.ShoppingCart;
import com.blueshift.reads.model.Book;
import com.bumptech.glide.Glide;
import com.github.rahulrvp.android_utils.EditTextUtils;
import com.github.rahulrvp.android_utils.TextViewUtils;


public class ProductDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "book";
    private EditText mQtyField;
    private Book mBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Blueshift.getInstance(this).trackScreenView(this, false);

        mBook = (Book) getIntent().getSerializableExtra(EXTRA_BOOK);
        if (mBook != null) {
            Blueshift.getInstance(this).trackProductView(mBook.getSku(), 1, false);

            ImageView bookCoverImage = (ImageView) findViewById(R.id.book_cover);
            if (bookCoverImage != null) {
                Glide
                        .with(this)
                        .load(mBook.getImageUrl())
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .crossFade()
                        .into(bookCoverImage);
            }

            TextView nameText = (TextView) findViewById(R.id.book_name);
            if (nameText != null) {
                nameText.setText(mBook.getName());
            }

            TextView priceText = (TextView) findViewById(R.id.book_price);
            TextViewUtils.setText(priceText, R.string.dollar_x, mBook.getPrice());

            TextView skuText = (TextView) findViewById(R.id.book_sku);
            if (skuText != null) {
                skuText.setText(mBook.getSku());
            }

            mQtyField = (EditText) findViewById(R.id.book_qty);
            if (mQtyField != null) {
                String qty = EditTextUtils.getText(mQtyField);
                if (!TextUtils.isEmpty(qty)) {
                    mQtyField.setSelection(qty.length());
                }
            }
        }
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
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        invalidateOptionsMenu();
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
