package com.blueshift.reads.framework;

import android.app.Application;

import com.blueshift.Blueshift;
import com.blueshift.model.Configuration;
import com.blueshift.reads.R;
import com.blueshift.reads.activity.ProductDetailsActivity;

/**
 * @author Rahul Raveendran V P
 *         Created on 12/10/16 @ 1:14 PM
 *         https://github.com/rahulrvp
 */


public class ReadsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Configuration configuration = new Configuration();
        configuration.setAppIcon(R.mipmap.ic_launcher);

        configuration.setProductPage(ProductDetailsActivity.class);
        // configuration.setCartPage(CartActivity.class);
        // configuration.setOfferDisplayPage(OfferDisplayActivity.class);
        // configuration.setDialogTheme(R.style.dialog_theme);
        // configuration.setBatchInterval(5 * 60 * 1000); // setting batch time as 5min

        configuration.setApiKey("5dfe3c9aee8b375bcc616079b08156d9"); // Bsft Reads
        // configuration.setApiKey("ae8087e9fb141de419ddbac09ed8b0a9"); // Bsft Mobile

        Blueshift.getInstance(this).initialize(configuration);
    }
}
