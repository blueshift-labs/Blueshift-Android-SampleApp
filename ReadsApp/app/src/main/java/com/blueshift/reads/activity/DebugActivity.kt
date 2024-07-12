package com.blueshift.reads.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blueshift.Blueshift
import com.blueshift.BlueshiftAppPreferences
import com.blueshift.BlueshiftExecutor
import com.blueshift.inappmessage.InAppApiCallback
import com.blueshift.model.UserInfo
import com.blueshift.reads.databinding.ActivityDebugBinding

class DebugActivity : AppCompatActivity() {
    lateinit var binding: ActivityDebugBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebugBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.inAppSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Blueshift.getInstance(this).registerForInAppMessages(this)
            } else {
                Blueshift.getInstance(this).unregisterForInAppMessages(this)
            }
        }

        binding.pushSwitch.isChecked = BlueshiftAppPreferences.getInstance(this).enablePush
        binding.inAppOptInSwitch.isChecked = BlueshiftAppPreferences.getInstance(this).enableInApp
        binding.trackSwitch.isChecked = Blueshift.isTrackingEnabled(this)

        binding.pushSwitch.setOnCheckedChangeListener { _, isChecked ->
            Blueshift.optInForPushNotifications(this, isChecked)
        }

        binding.inAppOptInSwitch.setOnCheckedChangeListener { _, isChecked ->
            Blueshift.optInForInAppNotifications(this, isChecked)
        }

        binding.trackSwitch.setOnCheckedChangeListener { _, isChecked ->
            Blueshift.setTrackingEnabled(this, isChecked)
        }
    }

    fun onClickFireEvents(view: View) {
        view.isEnabled = false

        val event = binding.debugEventSpinner.selectedItem
        Blueshift.getInstance(this).trackEvent(event as String, null, false)
        Toast.makeText(this, "Event sent.", Toast.LENGTH_SHORT).show()

        view.isEnabled = true
    }

    fun onClickFetchInApp(view: View) {
        view.isEnabled = false

        Blueshift.getInstance(this).fetchInAppMessages(
            object : InAppApiCallback {
                override fun onSuccess() {
                    Toast.makeText(view.context, "In app pulled successfully.", Toast.LENGTH_SHORT)
                        .show()
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
        view.isEnabled = false
        Toast.makeText(view.context, "Loop started", Toast.LENGTH_SHORT).show()

        send100Events(view.context)

        Toast.makeText(view.context, "Loop ended", Toast.LENGTH_SHORT).show()
        view.isEnabled = true
    }

    private fun multiThreadUseCase_50EventsInTwoLoopsOf25Each(context: Context) {
        val batchCount = 25
        BlueshiftExecutor.getInstance().runOnNetworkThread {
            for (i in 1..batchCount) {
                Blueshift.getInstance(context).trackEvent("event-$i", null, false)
            }
        }
        BlueshiftExecutor.getInstance().runOnNetworkThread {
            for (i in 1..batchCount) {
                Blueshift.getInstance(context).trackEvent("event-${i + batchCount}", null, false)
            }
        }
    }

    private fun multiThreadUseCase_50EventsInTwoLoops25RealtimeAnd25Batch(context: Context) {
        val batchCount = 25
        BlueshiftExecutor.getInstance().runOnNetworkThread {
            for (i in 1..batchCount) {
                Blueshift.getInstance(context).trackEvent("event-$i", null, false)
            }
        }
        BlueshiftExecutor.getInstance().runOnNetworkThread {
            for (i in 1..batchCount) {
                Blueshift.getInstance(context).trackEvent("event-${i + batchCount}", null, true)
            }
        }
    }

    private fun logoutAndLoginUseCase(context: Context) {
        val preferences = BlueshiftAppPreferences.getInstance(context)

        // simulate push and in-app opt out
        preferences.enablePush = false
        preferences.enableInApp = false
        Blueshift.getInstance(context).trackEvent("logout", null, false)

        // reset the device id (make sure the next event has this new device_id)
        Blueshift.resetDeviceId(context)

        // simulate push and in-app opt in
        preferences.enablePush = true
        preferences.enableInApp = true
        Blueshift.getInstance(context).trackEvent("login", null, false)

        preferences.save(context)
    }

    private fun send100Events(context: Context) {
        val batchCount = 100
        for (i in 1..batchCount) {
            Blueshift.getInstance(context).trackEvent("event-$i", null, false)
        }
    }

    fun logoutClick(view: View) {
        view.isEnabled = false

        val userInfo: UserInfo = UserInfo.getInstance(this)

        Blueshift.optInForInAppNotifications(this, false)
        Blueshift.optInForPushNotifications(this, false)

        Blueshift.getInstance(this).identifyUserByEmail(userInfo.email, null, false)

        userInfo.email = null
        userInfo.save(this)

        view.isEnabled = true

        finish()
    }

    fun requestPushPermission(view: View) {
        view.isEnabled = false
        Blueshift.requestPushNotificationPermission(this)
        view.isEnabled = true
    }
}