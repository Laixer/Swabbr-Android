package com.laixer.core

import android.app.IntentService
import android.content.Intent
import com.microsoft.windowsazure.messaging.NotificationHub
import com.microsoft.windowsazure.messaging.RegistrationGoneException
import com.google.firebase.iid.FirebaseInstanceId
import android.util.Log
import android.widget.Toast
import com.laixer.cache.MemoryCache
import java.util.concurrent.TimeUnit

class RegistrationIntentService : IntentService(TAG) {

    init {
        cache.save("registrationID", null)
        cache.save("fcmToken", null)
    }

    override fun onHandleIntent(p0: Intent?) {
        var resultString: String? = null
        var fcmToken: String? = null
        var regID: String?
        val storedToken: String?

        try {
            // Access the device registration token (Firebase)
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "getInstanceId failed", task.exception)
                        return@addOnCompleteListener
                    }

                    // Get new Instance ID token and save to cache
                    fcmToken = task.result?.token
                    cache.save("fcmToken", fcmToken)

                    val msg = getString(R.string.msg_token_fmt, fcmToken)
                    Log.d(TAG, msg)
                }
            TimeUnit.SECONDS.sleep(1)

            // Storing the registration ID that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server.
            // Otherwise, your server should have already received the token.
            regID = cache.load("registrationID")
            storedToken = cache.load("fcmToken")
            when {
                regID == null -> {

                    val hub = NotificationHub(
                        NotificationSettings.HubName,
                        NotificationSettings.HubListenConnectionString,
                        this
                    )

                    Log.d(TAG, "Attempting a new registration with NH using FCM token : $fcmToken")
                    regID = hub.register(fcmToken).registrationId

                    // If you want to use tags...
                    // Refer to : https://azure.microsoft.com/documentation/articles/notification-hubs-routing-tag-expressions/
                    // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                    resultString = "New NH Registration Successfully - RegId : " + regID!!
                    Log.d(TAG, resultString)

                    cache.save("registrationID", regID)
                    cache.save("fcmToken", fcmToken)
                }
                storedToken !== fcmToken -> {

                    val hub = NotificationHub(
                        NotificationSettings.HubName,
                        NotificationSettings.HubListenConnectionString, this
                    )
                    Log.d(TAG, "NH Registration refreshing with token : $fcmToken")
                    regID = hub.register(fcmToken).getRegistrationId()

                    // If you want to use tags...
                    // Refer to : https://azure.microsoft.com/documentation/articles/notification-hubs-routing-tag-expressions/
                    // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                    resultString = "New NH Registration Successfully - RegId : $regID"
                    Log.d(TAG, resultString)

                    cache.save("registrationID", regID)
                    cache.save("fcmToken", fcmToken)
                }
                else -> resultString = "Previously Registered Successfully - RegId : $regID"
            } // Check to see if the token has been compromised and needs refreshing.
        } catch (e: RegistrationGoneException) {
            Log.e(TAG, "Failed to complete registration", e)
            // If an exception happens while fetching the new token or updating registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }

        // Notify UI that registration has completed.
        if (MainActivity.isVisible) {
            Toast.makeText(MainActivity.mainActivity, resultString, Toast.LENGTH_LONG).show()
        }
    }

    object NotificationSettings {
        const val HubName = "swabbr"
        const val HubListenConnectionString =
            "Endpoint=sb://swabbr-notificationhubs.servicebus.windows.net/;" +
            "SharedAccessKeyName=DefaultListenSharedAccessSignature;" +
            "SharedAccessKey=FjhpRaNYK6WrnEYQKQcnfmXBLdUP/4xMVQaCnCIqzYQ="
    }

    companion object {
        private const val TAG = "RegIntentService"
        private var cache: MemoryCache<String?> = MemoryCache()
    }
}
