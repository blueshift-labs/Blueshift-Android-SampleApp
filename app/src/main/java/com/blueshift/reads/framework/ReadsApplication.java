package com.blueshift.reads.framework;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;

import com.blueshift.Blueshift;
import com.blueshift.BlueshiftAppPreferences;
import com.blueshift.BlueshiftLogger;
import com.blueshift.model.Configuration;
import com.blueshift.reads.BuildConfig;
import com.blueshift.reads.R;
import com.blueshift.reads.activity.PlaceOrderActivity;
import com.blueshift.reads.activity.ProductDetailsActivity;
import com.blueshift.util.BlueshiftUtils;

/**
 * @author Rahul Raveendran V P
 * Created on 12/10/16 @ 1:14 PM
 * https://github.com/rahulrvp
 */


public class ReadsApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*
        Helps to decide the level of logs from Blueshift Android SDK
         */
        BlueshiftLogger.setLogLevel(BlueshiftLogger.VERBOSE);

        /*
        Following is the initialization part
         */
        Configuration configuration = getBlueshiftConfiguration();
        configuration.setApiKey(BuildConfig.API_KEY); // Blueshift Event API Key
        Blueshift.getInstance(this).initialize(configuration);

        trackNewInstalls();

        /*
        Below are samples for overriding SDK controls.
         */
//        override clicks on in-app messages
//        Blueshift.getInstance(this).setInAppActionCallback(
//                new InAppActionCallback() {
//                    @Override
//                    public void onAction(String s, JSONObject jsonObject) {
//                        if ("open".equals(s)) {
//                            BlueshiftLogger.d("TAG", jsonObject.toString());
//                        }
//                    }
//                }
//        );

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
    }

    private Configuration getBlueshiftConfiguration() {
        Configuration configuration = new Configuration();

        // This icon will be used in Notification as icons & placeholder image,
        // if notification icons are not provided explicitly
        configuration.setAppIcon(R.mipmap.ic_launcher);

        // These methods are used for setting traditional deep-links with category
        // heads-up: these methods will be deprecated soon
        configuration.setProductPage(ProductDetailsActivity.class);
        configuration.setCartPage(PlaceOrderActivity.class);
        // configuration.setOfferDisplayPage(OfferDisplayActivity.class);
        // configuration.setDialogTheme(R.style.dialog_theme);

        // Following methods will let you set the large & small icons for Notification
        configuration.setLargeIconResId(R.mipmap.ic_launcher);
        configuration.setSmallIconResId(R.drawable.ic_notification);

        // The following method will let you decide the color to be used in the Notification
        configuration.setNotificationColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // Following methods will help you setup Notification channel for Android O and above.
        configuration.setDefaultNotificationChannelId("My-Notification-Channel-Id");
        configuration.setDefaultNotificationChannelName("My-Notification-Channel-Name");
        configuration.setDefaultNotificationChannelDescription("My-Notification-Channel-Description");

        // This method tells the sdk to fire an app_open event automatically when
        // user starts the application
        configuration.setEnableAutoAppOpenFiring(true);

        // This method allows you to disable push - default value is true
        configuration.setPushEnabled(true);

        // Following methods will help you set-up in-app messages
        configuration.setInAppEnabled(true);
        configuration.setJavaScriptForInAppWebViewEnabled(true);
        configuration.setInAppBackgroundFetchEnabled(true);
        // configuration.setInAppManualTriggerEnabled(true);

        // This method let's you decide the interval of batch event api calls.
        // Default value is 30min
        configuration.setBatchInterval(16 * 60 * 1000); // setting batch time as 16min

        // This method will let you decide what needs to be collected as device_id
        // The default value is AdvertisingId. You can change it to Firebase Instance Id
        // or a GUID using this method.
        configuration.setDeviceIdSource(Blueshift.DeviceIdSource.INSTANCE_ID);
//        configuration.setDeviceIdSource(Blueshift.DeviceIdSource.INSTANCE_ID_PKG_NAME);
//        configuration.setDeviceIdSource(Blueshift.DeviceIdSource.ADVERTISING_ID_PKG_NAME);
//        configuration.setDeviceIdSource(Blueshift.DeviceIdSource.GUID);

//        configuration.setDeviceIdSource(Blueshift.DeviceIdSource.CUSTOM);
//        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//        configuration.setCustomDeviceId(android_id);

        return configuration;
    }

    private void trackNewInstalls() {
        // app install check
        String PREF_FILE = "pref_file";
        SharedPreferences sp = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        if (sp != null) {
            String PREF_KEY = "pref_key";
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
