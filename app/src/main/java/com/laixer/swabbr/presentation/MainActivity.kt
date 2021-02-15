package com.laixer.swabbr.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.auth0.android.jwt.JWT
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.android.synthetic.main.activity_app.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *  Main activity in which everything is displayed using fragments,
 *  apart from logging in ([AuthActivity]) and recording a vlog from
 *  a notification ([RecordVlogActivity]).
 */
class MainActivity : AppCompatActivity() {
    private val vm: MainActivityViewModel by viewModel()

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_container_app) as NavHostFragment }
    private var navHostState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RxJavaPlugins.setErrorHandler {
            Log.e(TAG, it?.message ?: it?.message ?: "RxJava error")
            if (it?.cause?.message?.contains("401") == true) {
                vm.invalidateSession()
            }
        }

        // Set the top toolbar as the action bar and hide it immediately.
        setSupportActionBar(toolbar)
        supportActionBar?.hide()

        // We set the launch screen theme from manifest. After this we
        // need to get back to our Theme to remove the launch screen.
        setTheme(R.style.Theme_Swabbr)

        injectFeature()

        checkPlayServices()

        vm.authToken.observe(this, Observer(this@MainActivity::updateAppState))

        setContentView(R.layout.activity_app)

        val nc = navHostFragment.navController

        // Have the main app container be controlled by the bottom navigation bar.
        nav_host_container_app.post {
            NavigationUI.setupWithNavController(bottom_nav, navHostFragment.navController)
        }
    }

    override fun onResume() {
        super.onResume()
        vm.probeAuthToken()
    }

    // TODO This is an absolute mess. A lot of this code has been commented out
    //      because it introduced major navigation bugs, having two dashboards on
    //      top of each other. The entire auth functionality must be refactored.

    // TODO Here lies the bug that doesn't take us to the recording screen after a
    //      notification takes us to the login screen because we aren't logged in yet
    //      or don't have a valid jwt token anymore.

    // TODO This shouldn't handle our styling...
    /**
     *  Determines where we will go based on having/getting our
     *  access token after app entry of after login.
     */
    private fun updateAppState(res: Resource<JWT>) {
        when (res.state) {
            ResourceState.LOADING -> run {
                /* App auth is loading */
                navHostState = navHostFragment.navController.saveState() ?: navHostState
                // setTheme(R.style.Theme_Swabbr_Launcher) TODO
            }
            ResourceState.SUCCESS -> run {

                /* We can load the app */
                // setTheme(R.style.Theme_Swabbr) TODO

//                if (!navHostFragment.navController.popBackStack(R.id.dashboard_dest, false)) {
//                    // Force the user back to the login screen
//                    navHostFragment.navController.navigate(
//                        R.id.dashboard_dest,
//                        null,
//                        NavOptions.Builder().build()
//                    )
//                }
//
//                if (navHostState !== null) {
//                    navHostFragment.navController.restoreState(navHostState)
//                }

            }
            ResourceState.ERROR -> {

                // If we can, save the state so we can restore it when the user returns
                navHostState = navHostFragment.navController.saveState() ?: navHostState

                // We should try to reauthorize first if we can

                // Otherwise go to login
                navHostFragment.navController.navigate(R.id.auth_dest)
            }
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

    override fun onDestroy() {
        super.onDestroy()
        RxJavaPlugins.setErrorHandler(null)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    }
}
