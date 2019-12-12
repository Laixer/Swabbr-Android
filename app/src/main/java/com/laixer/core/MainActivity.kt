package com.laixer.core

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import com.laixer.navigation.features.SampleNavigation

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivity = this
        registerWithNotificationHubs()
        FirebaseService.createChannelAndHandleNotifications(applicationContext)

        FirebaseMessaging.getInstance().subscribeToTopic("DEV")

        startVlogList()
    }

    private fun registerWithNotificationHubs() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with FCM.
            val intent = Intent(this, RegistrationIntentService::class.java)
            startService(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        isVisible = true
    }

    override fun onPause() {
        super.onPause()
        isVisible = false
    }

    override fun onResume() {
        super.onResume()
        isVisible = true
    }

    override fun onStop() {
        super.onStop()
        isVisible = false
    }

    private fun startVlogList() = SampleNavigation.dynamicStart?.let { startActivity(it) }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog box that enables  users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                    .show()
            } else {
                Log.i(TAG, "This device is not supported by Google Play Services.")
                Toast.makeText(this, "This device is not supported by Google Play Services.", Toast.LENGTH_SHORT).show()
            }
            return false
        }
        return true
    }

    companion object {
        var mainActivity: MainActivity? = null
        var isVisible: Boolean = false
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        private const val TAG = "MainActivity"
    }
}
