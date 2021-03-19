package com.laixer.swabbr.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
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
     *  Dispose resources.
     */
    override fun onDestroy() {
        super.onDestroy()
        RxJavaPlugins.setErrorHandler(null)
    }
}
