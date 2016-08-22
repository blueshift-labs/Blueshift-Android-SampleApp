package com.blueshift.sampleapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.blueshift.Blueshift;

import com.blueshift.sampleapp.R;


public class ProductActivity extends ActionBarActivity {

    private String sku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        sku = getIntent().getStringExtra("sku");
        if (sku != null) {
            String mrp = getIntent().getStringExtra("mrp");
            String price = getIntent().getStringExtra("price");
            String displayMsg = "SKU: " + sku + "\nMRP: " + mrp + "\nPRICE: " + price;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Deep-link params");
            builder.setMessage(displayMsg);
            builder.setPositiveButton("Dismiss", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Blueshift.getInstance(this).trackScreenView(this);
        if (sku != null) {
            Blueshift.getInstance(this).trackProductView(sku, 10);
        } else {
            Blueshift.getInstance(this).trackProductView("S-007", 10);
        }
    }

    public void onBuyClicked(View view) {
        startActivity(new Intent(this, CartActivity.class));
    }

}
