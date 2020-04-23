package com.laixer.swabbr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.laixer.core.FirebaseService
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.presentation.AppActivity
import com.laixer.swabbr.presentation.auth.AuthActivity
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.model.AuthUserItem
import io.reactivex.plugins.RxJavaPlugins
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val vm: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPlayServices()
        RxJavaPlugins.setErrorHandler { it.localizedMessage }

        FirebaseService.createChannelAndHandleNotifications(baseContext)

        injectFeature()

        vm.run {
            authenticatedUser.observe(this@MainActivity, Observer { checkAuthentication(it) })
            get()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        vm.get()
    }

    override fun onResume() {
        vm.get()
        super.onResume()
    }

    override fun onRestart() {
        vm.get()
        super.onRestart()
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

    /**
     *  Check if authenticated to proceed, if not, redirect
     */
    private fun checkAuthentication(res: Resource<AuthUserItem>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {}
            ResourceState.SUCCESS -> proceed()
            ResourceState.ERROR -> authenticate()
        }
    }

    /**
     * Proceed with app flow
     */
    private fun proceed() = redirect(AppActivity::class.java)

    /**
     * Authenticate
     */
    private fun authenticate() = redirect(AuthActivity::class.java)

    /**
     * Redirect to to [AuthActivity]
     */
    private fun redirect(activity: Class<*>) = Intent(this, activity).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }.also {
        startActivity(it)
        finish()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    }
}
