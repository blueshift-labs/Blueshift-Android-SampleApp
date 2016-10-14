package com.blueshift.reads.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blueshift.Blueshift;
import com.blueshift.reads.R;
import com.blueshift.reads.model.Book;
import com.bumptech.glide.Glide;

import io.github.rahulrvp.android_utils.EditTextUtils;

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
            if (priceText != null) {
                priceText.setText(mBook.getPrice());
            }

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
                Blueshift.getInstance(this)
                        .trackAddToCart(mBook.getSku(), quantity, false);

                Toast.makeText(this, "Item added to cart.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
