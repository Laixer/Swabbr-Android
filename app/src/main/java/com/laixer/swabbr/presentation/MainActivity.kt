package com.laixer.swabbr.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.laixer.swabbr.NavGraphMainActivityDirections
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.auth.AuthFragment
import com.laixer.swabbr.services.play.PlayServicesChecker
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.android.synthetic.main.activity_main.*

/**
 *  Main activity in which everything is displayed using fragments.
 */
class MainActivity : AppCompatActivity() {

    /**
     *  Navigation fragment host, in which navigation can occur.
     *  This also contains our top-level navController.
     */
    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_container_app) as NavHostFragment
    }

    /**
     *  Flag to help with [tryRedirectToLogin] and [hasLoggedIn].
     */
    private var hasRedirected: Boolean = false

    /**
     *  Sets up the main activity. All our content will be displayed in here.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectFeature()

        // Exit if we aren't compatible with the play services.
        if (!PlayServicesChecker.checkPlayServices(this)) {
            onBackPressed()
        }

        // Setup the layout for this main activity.
        setContentView(R.layout.activity_main)

        // Have the main app container be controlled by the bottom navigation bar.
        nav_host_container_app.post {
            NavigationUI.setupWithNavController(bottom_navigation_view_main_activity, navHostFragment.navController)
        }
    }

    /**
     *  Called to try to redirect us to the login by [AuthFragment].
     *  Note that this will be called many times, hence the flags.
     */
    fun tryRedirectToLogin() {
        if (hasRedirected) {
            return
        }

        hasRedirected = true

        navHostFragment.navController.navigate(NavGraphMainActivityDirections.actionGlobalLoginFragment())
    }

    /**
     *  Call this to maintain consistent redirect behaviour.
     *  This will also be called many times.
     */
    fun hasLoggedIn() {
        if (hasRedirected) {
            hasRedirected = false
        }
    }

    /**
     *  Dispose resources.
     */
    override fun onDestroy() {
        super.onDestroy()
        RxJavaPlugins.setErrorHandler(null)
    }
}
