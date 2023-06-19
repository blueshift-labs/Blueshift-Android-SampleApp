package com.blueshift.reads.advanced

import android.os.Bundle
import com.blueshift.pn.BlueshiftNotificationEventsActivity


class CustomNotificationEventsActivityKotlin : BlueshiftNotificationEventsActivity() {
    override fun processAction(action: String?, extraBundle: Bundle?) {
        super.processAction(action, extraBundle)
        // Do the app specific actions here.
    }
}