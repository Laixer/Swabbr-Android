package com.laixer.swabbr.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.laixer.presentation.Resource
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.model.AuthUserItem
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/** Fragment that requires the user to be authenticated before use **/
open class AuthFragment : Fragment() {

    private val authVm: AuthViewModel by sharedViewModel()
    protected val authenticatedUser: AuthUserItem by lazy { authVm.authenticatedUser.value!!.data!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authVm.run {
            authenticatedUser.observe(this@AuthFragment, Observer { checkAuthentication() })
            get()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            authVm.get(true)
        }
    }

    override fun onResume() {
        super.onResume()
        authVm.run {
            get()
        }
    }

    protected fun onError(resource: Resource<Any?>) {
        if (resource.message?.contains("401") == true) {
            reauthorize()
        }
    }

    private fun checkAuthentication() {
        if (!authVm.isLoggedIn()) {
            reauthorize()
        }
    }

    private fun reauthorize() =
        findNavController().navigate(
            R.id.action_authenticate,
            null,
            NavOptions.Builder().setLaunchSingleTop(true).build()
        )
}
