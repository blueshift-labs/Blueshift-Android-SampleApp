package com.blueshift.reads;

import com.blueshift.fcm.BlueshiftMessagingService;
import com.blueshift.util.BlueshiftUtils;
import com.google.firebase.messaging.RemoteMessage;

public class ReadsMessagingService extends BlueshiftMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (BlueshiftUtils.isBlueshiftPushMessage(remoteMessage)) {
            super.onMessageReceived(remoteMessage);
        } else {
            /*
             * The push message does not belong to Blueshift. Please handle it here.
             */
        }
    }

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);

        /*
         * Use the new token in your app. the super.onNewToken() call is important
         * for the SDK to do the analytical part and notification rendering.
         * Make sure that it is present when you override onNewToken() method.
         */
    }
}
