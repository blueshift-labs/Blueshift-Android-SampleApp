package com.blueshift.reads.framework;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;

import com.blueshift.Blueshift;
import com.blueshift.BlueshiftInAppListener;
import com.blueshift.BlueshiftLogger;
import com.blueshift.BlueshiftPushListener;
import com.blueshift.BlueshiftRegion;
import com.blueshift.inappmessage.InAppApiCallback;
import com.blueshift.model.Configuration;
import com.blueshift.reads.BuildConfig;
import com.blueshift.reads.R;
import com.blueshift.util.BlueshiftUtils;
import com.google.gson.Gson;
import com.onesignal.OneSignal;

import java.util.Map;

/**
 * @author Rahul Raveendran V P
 * Created on 12/10/16 @ 1:14 PM
 * https://github.com/rahulrvp
 */


public class ReadsApplication extends Application {
    private static final String TAG = "ReadsApplication";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Optional: Enable Blueshift SDK's logging
        BlueshiftLogger.setLogLevel(BlueshiftLogger.VERBOSE);

        // Required: Initialize Blueshift SDK
        initializeBlueshiftSDK();

        // Advanced & optional: Attach push event callbacks
        // setBlueshiftPushCallbacks();

        // Advanced & optional: Attach in-app event callbacks
        // setBlueshiftInAppCallbacks();

        // Advanced & optional: Override in-app clicks
        // overrideBlueshiftInAppActions();

        // Advanced & optional: Call in-app api manually
        // invokeBlueshiftInAppApiCall();

        // Not part of SDK integration, test code.
        trackNewInstalls();

        initializeOneSignal();
    }

    private void initializeOneSignal() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(BuildConfig.OS_KEY);
    }
    private void initializeBlueshiftSDK() {
        Configuration configuration = new Configuration();
        configuration.setApiKey(BuildConfig.API_KEY);

        setOptionalConfigs(configuration);

        Blueshift.getInstance(this).initialize(configuration);
    }

    private void setOptionalConfigs(Configuration configuration) {
        // Following are optional configs.

        // This icon will be used in Notification as icons & placeholder image,
        // if notification icons are not provided explicitly
        configuration.setAppIcon(R.mipmap.ic_launcher);

        // Set Datacenter Region (v3.2.4 and above). Default: BlueshiftRegion.US.
        // Set region as EU
        // configuration.setRegion(BlueshiftRegion.EU);
        // Set region as US
        configuration.setRegion(BlueshiftRegion.US);

        // These methods are used for setting traditional deep-links with category
        // WARNING: The following methods are deprecated since v3.2.3 and will be removed in later versions
        // configuration.setProductPage(ProductDetailsActivity.class);
        // configuration.setCartPage(PlaceOrderActivity.class);
        // configuration.setOfferDisplayPage(OfferDisplayActivity.class);
        // configuration.setDialogTheme(R.style.dialog_theme);

        // This method tells the sdk to fire an app_open event automatically when
        // user starts the application
        configuration.setEnableAutoAppOpenFiring(true);

        // Configure the interval between two app_open events fired by the SDK.
        // By default, this value is 24h since SDK version 3.1.9
        configuration.setAutoAppOpenInterval(60);

        // Following methods will let you set the large & small icons for Notification
        configuration.setLargeIconResId(R.mipmap.ic_launcher);
        configuration.setSmallIconResId(R.drawable.ic_notification);

        // The following method will let you decide the color to be used in the Notification
        configuration.setNotificationColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // Following methods will help you setup Notification channel for Android O and above.
        // WARNING: It is highly recommended to set this on app level. If not provided in the
        // payload, these values will be used for creating Notification Channel. If not set,
        // SDK will put some default values to avoid skipping the notification.
        configuration.setDefaultNotificationChannelId("My-Notification-Channel-Id");
        configuration.setDefaultNotificationChannelName("My-Notification-Channel-Name");
        configuration.setDefaultNotificationChannelDescription("My-Notification-Channel-Description");

        // This method allows you to disable push - default value is true
        configuration.setPushEnabled(true);

        // Following methods will help you set-up in-app messages
        configuration.setInAppEnabled(true);
        configuration.setJavaScriptForInAppWebViewEnabled(true);
        // configuration.setInAppBackgroundFetchEnabled(false);
        // configuration.setInAppManualTriggerEnabled(true);

        // This method let's you decide the interval of batch event api calls.
        // Default value is 30min
        configuration.setBatchInterval(16 * 60 * 1000); // setting batch time as 16min

        // This method will let you decide what needs to be collected as device_id
        // The default value is AdvertisingId. You can change it to Firebase Instance Id
        // or a GUID using this method.
        configuration.setDeviceIdSource(Blueshift.DeviceIdSource.INSTANCE_ID);
        // configuration.setDeviceIdSource(Blueshift.DeviceIdSource.INSTANCE_ID_PKG_NAME);
        // configuration.setDeviceIdSource(Blueshift.DeviceIdSource.ADVERTISING_ID_PKG_NAME);
        // configuration.setDeviceIdSource(Blueshift.DeviceIdSource.GUID);

        // If the above options aren't sufficient, provide your own device id as custom device id.
        // configuration.setDeviceIdSource(Blueshift.DeviceIdSource.CUSTOM);
        // String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        // configuration.setCustomDeviceId(android_id);
    }

    private void setBlueshiftPushCallbacks() {
        Blueshift.setBlueshiftPushListener(new BlueshiftPushListener() {
            @Override
            public void onPushDelivered(Map<String, Object> map) {
                Log.d(TAG, "onPushDelivered: (map) " + new Gson().toJson(map));

                Map<String, String> attr = BlueshiftUtils.buildTrackApiAttributesFromPayload(map, getApplicationContext());
                Log.d(TAG, "onPushDelivered: " + new Gson().toJson(attr));
            }

            @Override
            public void onPushClicked(Map<String, Object> map) {
                Log.d(TAG, "onPushClicked: (map) " + new Gson().toJson(map));

                Map<String, String> attr = BlueshiftUtils.buildTrackApiAttributesFromPayload(map, getApplicationContext());
                Log.d(TAG, "onPushClicked: " + new Gson().toJson(attr));
            }
        });
    }

    private void setBlueshiftInAppCallbacks() {
        Blueshift.setBlueshiftInAppListener(new BlueshiftInAppListener() {
            @Override
            public void onInAppDelivered(Map<String, Object> map) {
                Log.d(TAG, "onInAppDelivered: (map)" + new Gson().toJson(map));

                Map<String, String> attr = BlueshiftUtils.buildTrackApiAttributesFromPayload(map, getApplicationContext());
                Log.d(TAG, "onInAppDelivered: " + new Gson().toJson(attr));
            }

            @Override
            public void onInAppOpened(Map<String, Object> map) {
                Log.d(TAG, "onInAppOpened: (map)" + new Gson().toJson(map));

                Map<String, String> attr = BlueshiftUtils.buildTrackApiAttributesFromPayload(map, getApplicationContext());
                Log.d(TAG, "onInAppOpened: " + new Gson().toJson(attr));
            }

            @Override
            public void onInAppClicked(Map<String, Object> map) {
                Log.d(TAG, "onInAppClicked: (map)" + new Gson().toJson(map));

                Map<String, String> attr = BlueshiftUtils.buildTrackApiAttributesFromPayload(map, getApplicationContext());
                Log.d(TAG, "onInAppClicked: " + new Gson().toJson(attr));
            }
        });
    }

    private void overrideBlueshiftInAppActions() {
        Blueshift.getInstance(this).setInAppActionCallback((action, params) -> {
                    if ("open".equals(action)) BlueshiftLogger.d(TAG, params.toString());
                }
        );
    }

    private void invokeBlueshiftInAppApiCall() {
        Blueshift.getInstance(this).fetchInAppMessages(new InAppApiCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: in-app");
            }

            @Override
            public void onFailure(int code, String message) {
                Log.d(TAG, "onFailure: in-app");
            }
        });
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
