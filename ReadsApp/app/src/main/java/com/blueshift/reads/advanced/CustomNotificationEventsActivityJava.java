package com.blueshift.reads.advanced;

import android.os.Bundle;

import com.blueshift.pn.BlueshiftNotificationEventsActivity;

public class CustomNotificationEventsActivityJava extends BlueshiftNotificationEventsActivity {
    @Override
    protected void processAction(String action, Bundle extraBundle) {
        super.processAction(action, extraBundle);
        // Do the app specific actions here.
    }
}
