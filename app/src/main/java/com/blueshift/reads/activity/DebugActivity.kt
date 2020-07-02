package com.blueshift.reads.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blueshift.Blueshift
import com.blueshift.inappmessage.InAppApiCallback
import com.blueshift.reads.R
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
}