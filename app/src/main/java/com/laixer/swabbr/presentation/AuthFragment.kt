package com.laixer.swabbr.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.laixer.presentation.Resource
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.model.AuthUserItem
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/** Fragment that forces authentication on create, view creation and resume **/
open class AuthFragment : Fragment() {

    private val authVm: AuthViewModel by sharedViewModel()
    protected val authenticatedUser: AuthUserItem by lazy { authVm.authenticatedUser.value!!.data!! }

    override fun onCreate(savedInstanceState: Bundle?) {

        authVm.run {
            authenticatedUser.observe(this@AuthFragment, Observer { checkAuthentication() })
            checkAuthentication()
            get()
        }

        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            authVm.get()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        authVm.get()
        super.onResume()
    }

    protected fun onError(resource: Resource<Any?>) {
        if (resource.message?.contains("401") == true) {
            Toast.makeText(requireContext(), "Session timed out, please sign in again", Toast.LENGTH_LONG).show()
            reauthorize()
        }
    }

    private fun checkAuthentication() {
        if (!authVm.isLoggedIn()) {
            Toast.makeText(requireContext(), "Please sign in.", Toast.LENGTH_LONG).show()
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
