package com.laixer.swabbr.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import kotlinx.android.synthetic.main.activity_app.*

class AppActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()

        checkPlayServices()

        setContentView(R.layout.activity_app)
        setSupportActionBar(toolbar)

        // Setup the bottom navigation view with a list of navigation graphs
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        nav_host_container.post {
            NavigationUI.setupWithNavController(bottom_nav, navHostFragment.navController)
        }
    }

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
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show()
            } else {
                Log.i(TAG, getString(R.string.google_play_unsupported_device))
                Toast.makeText(this, getString(R.string.google_play_unsupported_device), Toast.LENGTH_SHORT).show()
            }
            return false
        }
        return true
    }

    companion object {
        private const val TAG = "AppActivity"
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    }
}
