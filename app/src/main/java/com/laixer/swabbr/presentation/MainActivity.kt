package com.laixer.swabbr.presentation

import android.content.Intent
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

        // We set launch screen theme from manifest, we need to get back to our Theme to remove
        // launch screen.
        setTheme(R.style.Theme_Swabbr)

        injectFeature()

        checkPlayServices()

        vm.authToken.observe(this, Observer(this@MainActivity::updateAppState))

        setContentView(R.layout.activity_app)
        setSupportActionBar(toolbar)

        // Setup the bottom navigation view with a list of navigation graphs
        nav_host_container_app.post {
            NavigationUI.setupWithNavController(bottom_nav, navHostFragment.navController)
        }
    }

    private fun updateAppState(res: Resource<JWT>) {
        when (res.state) {
            ResourceState.LOADING -> run {
                /* App auth is loading */
                navHostState = navHostFragment.navController.saveState() ?: navHostState
                setTheme(R.style.Theme_Swabbr_Launcher)
            }
            ResourceState.SUCCESS -> run {

                /* We can load the app */
                setTheme(R.style.Theme_Swabbr)

                if (!navHostFragment.navController.popBackStack(R.id.dashboard_dest, false)) {
                    // Force the user back to the login screen
                    navHostFragment.navController.navigate(
                        R.id.dashboard_dest,
                        null,
                        NavOptions.Builder().build()
                    )
                }

                if (navHostState !== null) {
                    navHostFragment.navController.restoreState(navHostState)
                }

            }
            ResourceState.ERROR -> {

                // If we can, save the state so we can restore it when the user returns
                navHostState = navHostFragment.navController.saveState() ?: navHostState

                // We should try to reauthorize first if we can

                // Otherwise go to login
                navHostFragment.navController.navigate(R.id.authAcitivty)
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

    companion object {
        private const val TAG = "MainActivity"
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    }


}

