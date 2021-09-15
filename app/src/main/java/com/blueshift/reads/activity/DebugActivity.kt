package com.blueshift.reads.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blueshift.Blueshift
import com.blueshift.BlueshiftAppPreferences
import com.blueshift.inappmessage.InAppApiCallback
import com.blueshift.model.UserInfo
import com.blueshift.reads.BuildConfig
import com.blueshift.reads.R
import com.blueshift.reads.framework.BSFTUtil
import com.blueshift.util.CommonUtils
import kotlinx.android.synthetic.main.activity_debug.*

class DebugActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        inAppSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Blueshift.getInstance(this).registerForInAppMessages(this)
            } else {
                Blueshift.getInstance(this).unregisterForInAppMessages(this)
            }
        }

        pushSwitch.isChecked = BlueshiftAppPreferences.getInstance(this).enablePush
        inAppOptInSwitch.isChecked = BlueshiftAppPreferences.getInstance(this).enableInApp
        trackSwitch.isChecked = Blueshift.isTrackingEnabled(this)

        pushSwitch.setOnCheckedChangeListener { _, isChecked ->
            Blueshift.optInForPushNotifications(this, isChecked)
        }

        inAppOptInSwitch.setOnCheckedChangeListener { _, isChecked ->
            Blueshift.optInForInAppNotifications(this, isChecked)
        }

        trackSwitch.setOnCheckedChangeListener { _, isChecked ->
            Blueshift.setTrackingEnabled(this, isChecked)
        }
    }

    fun onClickFireEvents(view: View) {
        view.isEnabled = false

        val event = debugEventSpinner.selectedItem
        Blueshift.getInstance(this).trackEvent(event as String, null, false)
        Toast.makeText(this, "Event sent.", Toast.LENGTH_SHORT).show()

        view.isEnabled = true
    }

    fun onClickFetchInApp(view: View) {
        view.isEnabled = false

        Blueshift.getInstance(this).fetchInAppMessages(
                object : InAppApiCallback {
                    override fun onSuccess() {
                        Toast.makeText(view.context, "In app pulled successfully.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(p0: Int, p1: String?) {
                        Toast.makeText(view.context, "In app pull failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        )

        view.isEnabled = true
    }

    fun onClickShowInApp(view: View) {
        view.isEnabled = false

        Blueshift.getInstance(this).displayInAppMessages()

        view.isEnabled = true
    }

    fun fireHundredEvents(view: View) {
        // assume we make an api call here and get the api key
        BSFTUtil.cacheApiKey(this, BuildConfig.API_KEY)
        BSFTUtil.initBlueshift(this)
        Blueshift.getInstance(this).identifyUser(null, false)

//        view.isEnabled = false
//        Toast.makeText(view.context, "Loop started", Toast.LENGTH_SHORT).show()
//        for (i in 0..100) {
//            Blueshift.getInstance(view.context).trackEvent("bsft_hundred_event", null, false)
//        }
//
//        Toast.makeText(view.context, "Loop ended", Toast.LENGTH_SHORT).show()
//        view.isEnabled = true
    }

    fun logoutClick(view: View) {
        val userInfo: UserInfo = UserInfo.getInstance(this)

        Blueshift.optInForInAppNotifications(this, false)
        Blueshift.optInForPushNotifications(this, false)

        Blueshift.getInstance(this).identifyUserByEmail(userInfo.email, null, false)

        userInfo.email = null
        userInfo.save(this)

        finish()
    }
}