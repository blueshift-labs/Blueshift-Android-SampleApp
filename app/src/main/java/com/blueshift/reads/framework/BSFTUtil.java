package com.blueshift.reads.framework;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.content.ContextCompat;

import com.blueshift.Blueshift;
import com.blueshift.model.Configuration;
import com.blueshift.reads.R;

public class BSFTUtil {


    public static void initBlueshift(Context context) {
        String apiKey = getCachedApiKey(context);
        if (apiKey != null) {
            initializeBlueshiftSDK(context, apiKey);
        }
    }

    private static String getCachedApiKey(Context context) {
        SharedPreferences sp = context.getSharedPreferences("apiKeyFile", Context.MODE_PRIVATE);
        return sp.getString("blueshiftApiKey", null);
    }

    public static void cacheApiKey(Context context, String apiKey) {
        SharedPreferences sp = context.getSharedPreferences("apiKeyFile", Context.MODE_PRIVATE);
        sp.edit().putString("blueshiftApiKey", apiKey).apply();
    }

    private static void initializeBlueshiftSDK(Context context, String apiKey) {
        Configuration configuration = new Configuration();
        configuration.setApiKey(apiKey);

        setOptionalConfigs(context, configuration);

        Blueshift.getInstance(context).initialize(configuration);
    }

    private static void setOptionalConfigs(Context context, Configuration configuration) {
        // Following are optional configs.

        // This icon will be used in Notification as icons & placeholder image,
        // if notification icons are not provided explicitly
        configuration.setAppIcon(R.mipmap.ic_launcher);

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
        configuration.setNotificationColor(ContextCompat.getColor(context, R.color.colorPrimary));

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


}
