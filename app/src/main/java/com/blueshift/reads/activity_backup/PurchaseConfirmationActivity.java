package com.blueshift.reads.activity_backup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blueshift.Blueshift;
import com.blueshift.model.Product;
import com.blueshift.reads.R;
import com.blueshift.reads.activity.ProductListActivity;


public class PurchaseConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_confirmation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Blueshift.getInstance(this).trackScreenView(this, true);

        Product[] products = new Product[3];

        for (int i = 0; i < 3; i++) {
            Product product = new Product();
            product.setPrice(i * 10.6f);
            product.setQuantity(i);
            product.setSku("S-00" + (i + 1));
            products[i] = product;
        }

        Blueshift.getInstance(this).trackProductsPurchase("S123456789", products, 12.56f, 10.86f, 5.86f, "CB007", false);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
