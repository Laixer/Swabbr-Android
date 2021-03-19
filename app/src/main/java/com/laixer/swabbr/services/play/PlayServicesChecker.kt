package com.laixer.swabbr.services.play

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.laixer.swabbr.R

class PlayServicesChecker {
    companion object {
        // TODO Why would we ever need this?
        /**
         *  Check the device to make sure it has the Google Play Services APK. If it
         *  doesn't, display a dialog box that enables  users to download the APK
         *  from the Google Play Store or enable it in the device's system settings.
         */
        fun checkPlayServices(activity: Activity): Boolean {
            val apiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = apiAvailability.isGooglePlayServicesAvailable(activity)
            if (resultCode != ConnectionResult.SUCCESS) {
                if (apiAvailability.isUserResolvableError(resultCode)) {
                    apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show()
                } else {
                    Log.i(TAG, activity.applicationContext.getString(R.string.google_play_unsupported_device))
                    Toast.makeText(
                        activity.applicationContext,
                        activity.applicationContext.getString(R.string.google_play_unsupported_device),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return false
            }
            return true
        }

        private const val TAG = "PlayServicesChecker"
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    }
}
