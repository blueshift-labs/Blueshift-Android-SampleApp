package com.blueshift.reads.advanced;

import com.blueshift.fcm.BlueshiftMessagingService;
import com.blueshift.util.BlueshiftUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class CustomMessagingServiceJava extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (BlueshiftUtils.isBlueshiftPushMessage(remoteMessage)) {
            BlueshiftMessagingService.handleMessageReceived(this, remoteMessage);
        } else {
            // The push message does not belong to Blueshift. Please handle it here.
        }
    }

    @Override
    public void onNewToken(String newToken) {
        BlueshiftMessagingService.handleNewToken(newToken);

        // Use the new token in your app if needed.
    }
}
