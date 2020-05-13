package com.laixer.swabbr.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.laixer.presentation.Resource
import com.laixer.swabbr.MainActivity
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.model.AuthUserItem
import kotlinx.android.synthetic.main.activity_app.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppActivity : AppCompatActivity() {

    private val vm: AuthViewModel by viewModel()
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()

        setContentView(R.layout.activity_app)
        setSupportActionBar(toolbar)

        vm.authenticatedUser.observe(this, Observer { checkAuthentication(it) })

        val navGraphIds = listOf(
            R.navigation.nav_graph_dashboard,
            R.navigation.nav_graph_search,
            R.navigation.nav_graph_vlogs,
            R.navigation.nav_graph_profile
        )
        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottom_nav.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )
        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            setupActionBarWithNavController(navController)
        })
        currentNavController = controller
    }

    fun checkAuthentication(res: Resource<AuthUserItem?>) {
        if (res.data == null) {
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.also {
                startActivity(it)
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean = currentNavController?.value?.navigateUp() ?: false

    companion object {
        private const val TAG = "AppActivity"
    }
}
