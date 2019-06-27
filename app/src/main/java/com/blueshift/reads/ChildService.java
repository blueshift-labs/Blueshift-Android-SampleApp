package com.blueshift.reads;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.blueshift.pn.BlueshiftNotificationEventsService;

/**
 * @author Rahul Raveendran V P
 * Created on 27/12/17 @ 11:57 AM
 * https://github.com/rahulrvp
 */


public class ChildService extends BlueshiftNotificationEventsService {

    @Override
    public void openApp(Context context, Bundle bundle) {
        Log.d("XXX", "Open app");
    }
}
