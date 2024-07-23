package com.blueshift.reads.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blueshift.Blueshift
import com.blueshift.BlueshiftAppPreferences
import com.blueshift.BlueshiftExecutor
import com.blueshift.BlueshiftLogger
import com.blueshift.model.UserInfo
import com.blueshift.reads.activity.ui.theme.ReadsAppTheme
import com.blueshift.rich_push.Message

class VerifierActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReadsAppTheme {
                val list = listOf(CardItem(
                    "Send 100 events", listOf(
                        "Send 100 events in a loop.",
                        "Verify the events are inserted in the same order as they were tracked.",
                        "Verify the API calls are made in the same order as the events were tracked."
                    )
                ) {
                    val batchCount = 100
                    for (i in 1..batchCount) {
                        Blueshift.getInstance(applicationContext)
                            .trackEvent("realtime_event_$i", null, false)
                    }
                }, CardItem(
                    "Send 200 batch events", listOf(
                        "Send 200 batch events in a loop.",
                        "Verify the events are inserted in the same order as they were tracked.",
                        "Verify the events are sent in two batches (100 events each).",
                        "Verify the events in the payload also follows the same order in which they were tracked."
                    )
                ) {
                    val batchCount = 200
                    for (i in 1..batchCount) {
                        Blueshift.getInstance(applicationContext)
                            .trackEvent("batch_event_$i", null, true)
                    }
                }, CardItem(
                    "Send realtime & batch events in parallel", listOf(
                        "Send 10 events each for realtime and batch in different threads.",
                        "Verify the events are inserted in the same order as they were tracked.",
                        "Verify the realtime events are attempted immediately",
                        "Verify the batch events are sent in a batch of 10 events."
                    )
                ) {
                    val batchCount = 10
                    BlueshiftExecutor.getInstance().runOnNetworkThread {
                        for (i in 1..batchCount) {
                            Blueshift.getInstance(applicationContext)
                                .trackEvent("parallel_realtime_event_$i", null, false)
                        }
                    }
                    BlueshiftExecutor.getInstance().runOnNetworkThread {
                        for (i in 1..batchCount) {
                            Blueshift.getInstance(applicationContext)
                                .trackEvent("parallel_batch_event_${i}", null, true)
                        }
                    }
                }, CardItem(
                    "Logout and Login use case (push, in-app and device_id)", listOf(
                        "Opt out of push and in-app and send a logout event.",
                        "Reset the device id, opt-in for push and in-app and send a login event.",
                        "Verify the events are sent in correct order and with proper values for enable_inapp and enable_push",
                        "Verify the device_id is also updated after the reset"
                    )
                ) {
                    val preferences = BlueshiftAppPreferences.getInstance(applicationContext)

                    // simulate push and in-app opt out
                    preferences.enablePush = false
                    preferences.enableInApp = false
                    Blueshift.getInstance(applicationContext).trackEvent("logout", null, false)
                    Blueshift.getInstance(applicationContext).identifyUser(null, false)

                    // reset the device id (make sure the next event has this new device_id)
                    Blueshift.resetDeviceId(applicationContext)

                    // simulate push and in-app opt in
                    preferences.enablePush = true
                    preferences.enableInApp = true
                    Blueshift.getInstance(applicationContext).trackEvent("login", null, false)
                    Blueshift.getInstance(applicationContext).identifyUser(null, false)

                    preferences.save(applicationContext)
                }, CardItem(
                    "Logout and Login use case (user info changes)", listOf(
                        "Clear the user info and send a logout event.",
                        "Verify that the logout event does not contain any user info.",
                        "Set the user info (email and customer id) and send a login event.",
                        "Verify that the login event contains the user info."
                    )
                ) {
                    val userInfo = UserInfo.getInstance(applicationContext)
                    val email = userInfo.email
                    val customerId = userInfo.retailerCustomerId

                    BlueshiftLogger.d(
                        "",
                        "user info before clear(): email = ${userInfo.email}, customer_id = ${userInfo.retailerCustomerId}"
                    )
                    userInfo.clear(applicationContext)
                    BlueshiftLogger.d(
                        "",
                        "user info after clear(): email = ${userInfo.email}, customer_id = ${userInfo.retailerCustomerId}"
                    )

                    Blueshift.getInstance(applicationContext).trackEvent("logout", null, false)
                    Blueshift.getInstance(applicationContext).identifyUser(null, false)

                    userInfo.email = email
                    userInfo.retailerCustomerId = customerId
                    Blueshift.getInstance(applicationContext).trackEvent("login", null, false)
                    Blueshift.getInstance(applicationContext).identifyUser(null, false)

                    userInfo.save(applicationContext)
                }, CardItem(
                    "Offline use cases", listOf(
                        "Realtime & batch events should be cached and sent later in batch events.",
                        "Campaign events should be cached and sent separately."
                    )

                ) {
                    Blueshift.getInstance(applicationContext)
                        .trackEvent("offline_realtime_event", null, false)
                    Blueshift.getInstance(applicationContext)
                        .trackEvent("offline_batch_event", null, true)

                    val message = Message()
                    Blueshift.getInstance(applicationContext).trackNotificationClick(message)
                })
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    LazyColumn(modifier = Modifier.padding(padding)) {
                        items(list) { cardItem ->
                            Card(modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                                .clickable { cardItem.onClick() }) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = cardItem.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    cardItem.description.forEach {
                                        Row {
                                            Text(
                                                text = "*",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Spacer(modifier = Modifier.size(8.dp))
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class CardItem(val title: String, val description: List<String>, val onClick: () -> Unit)