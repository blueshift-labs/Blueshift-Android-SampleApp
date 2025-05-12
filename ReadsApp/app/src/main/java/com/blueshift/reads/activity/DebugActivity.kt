package com.blueshift.reads.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blueshift.Blueshift
import com.blueshift.BlueshiftAppPreferences
import com.blueshift.BlueshiftExecutor
import com.blueshift.inappmessage.InAppApiCallback
import com.blueshift.model.UserInfo
import com.blueshift.reads.databinding.ActivityDebugBinding
import com.blueshift.reads.framework.EdgeToEdgeHelper

class DebugActivity : AppCompatActivity() {
    lateinit var binding: ActivityDebugBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup edge-to-edge display
        EdgeToEdgeHelper.setupEdgeToEdge(this)
        
        binding = ActivityDebugBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        
        // Apply window insets to the root view
        EdgeToEdgeHelper.applyWindowInsets(view)

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

    fun requestPushPermission(view: View) {
        view.isEnabled = false
        Blueshift.requestPushNotificationPermission(this)
        view.isEnabled = true
    }
}