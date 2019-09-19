package com.blueshift.reads.framework;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;

import com.blueshift.Blueshift;
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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

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

        configuration.setPushEnabled(true);

        configuration.setInAppEnabled(true);
        configuration.setJavaScriptForInAppWebViewEnabled(true);
//        configuration.setInAppManualTriggerEnabled(true);
        configuration.setInAppBackgroundFetchEnabled(true);

        // configuration.setOfferDisplayPage(OfferDisplayActivity.class);
        // configuration.setDialogTheme(R.style.dialog_theme);
        configuration.setBatchInterval(16 * 60 * 1000); // setting batch time as 16min

        configuration.setApiKey(BuildConfig.API_KEY);

        Blueshift.getInstance().initialize(getApplicationContext(), configuration);

        // overriding the in-ap api call
//        Blueshift
//                .getInstance(this)
//                .fetchInAppMessages(new InAppApiCallback() {
//                    @Override
//                    public void onApiCallComplete() {
//
//                    }
//                });

        // overriding the clicks on in-app
//        Blueshift
//                .getInstance(this)
//                .setInAppActionCallback(new InAppActionCallback() {
//                    @Override
//                    public void onAction(String name, JSONObject args) {
//                        Log.d("Application", "InApp Action. name: " + name + ", args: " + args);
//                        // name - name of the action
//                        // args - the arguments for the action
//                    }
//                });

        // app install check
        SharedPreferences sp = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        if (sp != null) {
            boolean isNewInstall = sp.getBoolean(PREF_KEY, true);
            if (isNewInstall) {
                // call event
                Blueshift.getInstance().trackEvent("bsft_newinstall", null, false);

                // update sp
                sp.edit().putBoolean(PREF_KEY, false).apply();
            }
        }
    }
}
