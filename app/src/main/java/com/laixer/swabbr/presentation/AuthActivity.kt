package com.laixer.swabbr.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.model.TokenWrapper
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.auth.AuthViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.system.exitProcess

/**
 *  Activity for authenticating the user, meaning login,
 *  registration and logout.
 */
class AuthActivity : AppCompatActivity() {
    private val vm: AuthViewModel by viewModel()
    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_container_auth) as NavHostFragment }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectFeature()

        // TODO Question --> What exactly does this imply?
        //      Before refactor this observed vm.authenticatedUser
        vm.tokenWrapper.observe(this, Observer(this@AuthActivity::login))

        setContentView(R.layout.activity_auth)
    }

    // TODO Fix loading icon?
    private fun login(res: Resource<TokenWrapper?>) {
        when (res.state) {
            ResourceState.LOADING -> {
                // video_playback_loading_icon.visible()
            }
            ResourceState.SUCCESS -> {
                // video_playback_loading_icon.gone()
                res.data?.jwtToken?.let {
                    navHostFragment.navController.navigate(R.id.mainActivity)
                }
            }
            ResourceState.ERROR -> {
                // video_playback_loading_icon.gone()
                passwordInput.text.clear()
                Log.e(TAG, res.message!!)
                Toast.makeText(applicationContext, res.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        exitProcess(0)
    }

    companion object {
        const val TAG = "AuthActivity"
    }
}
