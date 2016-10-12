package com.blueshift.reads;

import android.app.Application;

import com.blueshift.Blueshift;
import com.blueshift.model.Configuration;

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

        // configuration.setProductPage(ProductActivity.class);
        // configuration.setCartPage(CartActivity.class);
        // configuration.setOfferDisplayPage(OfferDisplayActivity.class);
        // configuration.setDialogTheme(R.style.dialog_theme);
        // configuration.setBatchInterval(5 * 60 * 1000); // setting batch time as 5min

        configuration.setApiKey("ae8087e9fb141de419ddbac09ed8b0a9"); // this is a test key. use a real one here.

        Blueshift.getInstance(this).initialize(configuration);
    }
}
