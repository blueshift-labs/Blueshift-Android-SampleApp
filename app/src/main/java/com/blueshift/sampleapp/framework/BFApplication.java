package com.blueshift.sampleapp.framework;

import android.app.Application;

import com.blueshift.Blueshift;
import com.blueshift.model.Configuration;

import com.blueshift.sampleapp.R;
import com.blueshift.sampleapp.activity.CartActivity;
import com.blueshift.sampleapp.activity.OfferDisplayActivity;
import com.blueshift.sampleapp.activity.ProductActivity;

import java.util.ArrayList;

/**
 * Created by rahul on 17/2/15.
 */
public class BFApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Configuration configuration = new Configuration();
        configuration.setAppIcon(R.mipmap.ic_launcher);
        configuration.setProductPage(ProductActivity.class);
        configuration.setCartPage(CartActivity.class);
        configuration.setOfferDisplayPage(OfferDisplayActivity.class);
        configuration.setApiKey("ae8087e9fb141de419ddbac09ed8b0a9"); // this is a test key. use a real one here.

        Blueshift.getInstance(this).initialize(configuration);
    }
}
