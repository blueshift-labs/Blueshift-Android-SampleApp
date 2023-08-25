package com.blueshift.reads.advanced

import com.blueshift.fcm.BlueshiftMessagingService
import com.blueshift.util.BlueshiftUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CustomMessagingServiceKotlin : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (BlueshiftUtils.isBlueshiftPushMessage(remoteMessage)) {
            BlueshiftMessagingService.handleMessageReceived(this, remoteMessage)
        } else {
            // The push message does not belong to Blueshift. Please handle it here.
        }
    }

    override fun onNewToken(newToken: String) {
        BlueshiftMessagingService.handleNewToken(newToken)

        // Use the new token in your app if needed.
    }
}