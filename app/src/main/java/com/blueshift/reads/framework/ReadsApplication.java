package com.blueshift.reads.framework;

import android.app.Application;
import android.support.v4.content.ContextCompat;

import com.blueshift.Blueshift;
import com.blueshift.model.Configuration;
import com.blueshift.reads.BuildConfig;
import com.blueshift.reads.R;
import com.blueshift.reads.activity.PlaceOrderActivity;
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
        configuration.setCartPage(PlaceOrderActivity.class);

        // configuration.setLargeIconResId(R.mipmap.ic_launcher);
        configuration.setSmallIconResId(R.drawable.notification_small_icon);

        configuration.setNotificationColor(ContextCompat.getColor(this, R.color.colorAccent));

        // configuration.setOfferDisplayPage(OfferDisplayActivity.class);
        // configuration.setDialogTheme(R.style.dialog_theme);
        // configuration.setBatchInterval(5 * 60 * 1000); // setting batch time as 5min

        configuration.setApiKey(BuildConfig.API_KEY);

        Blueshift.getInstance(this).initialize(configuration);
    }
}
