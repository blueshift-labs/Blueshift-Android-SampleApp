package ch.bullfin.blueshiftandroidapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.blueshift.Blueshift;
import com.blueshift.model.Product;
import com.blueshift.rich_push.Message;
import com.google.gson.Gson;

import ch.bullfin.blueshiftandroidapp.R;

public class CartActivity extends ActionBarActivity {

    private String sku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        String displayMsg = null;

        sku = getIntent().getStringExtra("sku");
        if (sku != null) {
            String mrp = getIntent().getStringExtra("mrp");
            String price = getIntent().getStringExtra("price");
            displayMsg = "SKU: " + sku + "\nMRP: " + mrp + "\nPRICE: " + price;
        } else {
            Message message = (Message) getIntent().getSerializableExtra("message");
            if (message != null) {
                displayMsg = new Gson().toJson(message);
            }
        }

        if (displayMsg != null) {
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
            Blueshift.getInstance(this).trackAddToCart(sku, 1);
        } else {
            Blueshift.getInstance(this).trackAddToCart("S-007", 1);
        }
    }

    public void onPurchaseConfirmationClicked(View view) {
        Product[] products = new Product[3];

        for (int i = 0; i < 3; i++) {
            Product product = new Product();
            product.setPrice(i * 10.6f);
            product.setQuantity(i);
            product.setSku("S-00" + (i + 1));
            products[i] = product;
        }
        Blueshift.getInstance(this).trackCheckoutCart(products, 12.56f, 5.86f, "CB007");

        startActivity(new Intent(this, PurchaseConfirmationActivity.class));
    }
}
