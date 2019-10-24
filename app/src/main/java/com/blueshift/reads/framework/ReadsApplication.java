package com.blueshift.reads.framework;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.blueshift.Blueshift;
import com.blueshift.inappmessage.InAppApiCallback;
import com.blueshift.model.Configuration;
import com.blueshift.reads.BuildConfig;
import com.blueshift.reads.R;
import com.blueshift.reads.activity.PlaceOrderActivity;
import com.blueshift.reads.activity.ProductDetailsActivity;

/**
 * @author Rahul Raveendran V P
 * Created on 12/10/16 @ 1:14 PM
 * https://github.com/rahulrvp
 */


public class ReadsApplication extends Application {

    private final String PREF_FILE = "pref_file";
    private final String PREF_KEY = "pref_key";

    @Override
    public void onCreate() {
        super.onCreate();

        Configuration configuration = new Configuration();
        configuration.setAppIcon(R.mipmap.ic_launcher);

        configuration.setProductPage(ProductDetailsActivity.class);
        configuration.setCartPage(PlaceOrderActivity.class);

        configuration.setLargeIconResId(R.mipmap.ic_launcher);
        configuration.setSmallIconResId(R.drawable.ic_notification);

        configuration.setNotificationColor(ContextCompat.getColor(this, R.color.colorPrimary));

        configuration.setDefaultNotificationChannelName("Random");
        configuration.setEnableAutoAppOpenFiring(true);

        configuration.setInAppEnabled(true);
        configuration.setJavaScriptForInAppWebViewEnabled(true);
        configuration.setInAppManualTriggerEnabled(true);

        // configuration.setOfferDisplayPage(OfferDisplayActivity.class);
        // configuration.setDialogTheme(R.style.dialog_theme);
        configuration.setBatchInterval(16 * 60 * 1000); // setting batch time as 16min

        configuration.setApiKey(BuildConfig.API_KEY);

        Blueshift.getInstance(this).initialize(configuration);

        // app install check
        SharedPreferences sp = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        if (sp != null) {
            boolean isNewInstall = sp.getBoolean(PREF_KEY, true);
            if (isNewInstall) {
                // call event
                Blueshift.getInstance(this).trackEvent("bsft_newinstall", null, false);

                // update sp
                sp.edit().putBoolean(PREF_KEY, false).apply();
            }
        }
    }
}
