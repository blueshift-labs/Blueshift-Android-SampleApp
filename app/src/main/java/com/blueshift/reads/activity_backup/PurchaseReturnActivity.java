package com.blueshift.reads.activity_backup;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.blueshift.Blueshift;
import com.blueshift.model.Product;
import com.blueshift.reads.R;

public class PurchaseReturnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_return);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Blueshift.getInstance(this).trackScreenView(this, true);
    }

    public void onPurchaseReturnClicked(View view) {
        Product[] products = new Product[2];

        for (int i = 0; i < 2; i++) {
            Product product = new Product();
            product.setPrice(i * 10.6f);
            product.setQuantity(i);
            product.setSku("S-00" + (i + 1));
            products[i] = product;
        }

        Blueshift.getInstance(this).trackPurchaseReturn("S123456789", products, false);
    }

    public void onPurchaseCancelClicked(View view) {
        Blueshift.getInstance(this).trackPurchaseCancel("S123456789", false);
    }


}
