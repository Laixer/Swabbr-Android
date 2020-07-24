package com.laixer.swabbr.presentation

import android.accounts.AccountManager
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.model.AuthUserItem
import com.laixer.swabbr.presentation.model.hasValidSession
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/** Fragment that forces authentication on create, view creation and resume **/
open class AuthFragment : Fragment() {

//    private val am: AccountManager = AccountManager.get(requireContext())
    private val options = Bundle()
    private val authVm: AuthViewModel by sharedViewModel()
    protected val authenticatedUser: AuthUserItem by lazy { authVm.authenticatedUser.value!!.data!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        injectFeature()
        authVm.run {
            authenticatedUser.observe(this@AuthFragment, Observer { checkAuthentication(it) })
            get()
        }

        super.onCreate(savedInstanceState)
    }

    protected fun onError(resource: Resource<Any?>) {
        if (resource.message?.contains("401") == true) {
            reauthorize()
        }
    }

    private fun checkAuthentication(res: Resource<AuthUserItem?>) {
//        val accounts = am.getAccountsByType(ACCOUNT_TYPE)
        try {
            when (res.state) {
                ResourceState.LOADING -> {
                }
                ResourceState.SUCCESS -> {
                    require(res.data?.hasValidSession() ?: false) { "HAS TO LOGIN " }
                }
                ResourceState.ERROR -> {
                    Toast.makeText(requireContext(), "Please sign in.", Toast.LENGTH_LONG).show()
                    reauthorize()
                }
            }
        } catch (e: IllegalArgumentException) {
            reauthorize()
        }
    }

    private fun reauthorize() =
        findNavController().navigate(
            R.id.action_authenticate,
            null,
            NavOptions.Builder().setLaunchSingleTop(true).build()
        ).also {
            Toast.makeText(requireContext(), "Please sign in.", Toast.LENGTH_LONG).show()
        }

    companion object {
        private val ACCOUNT_TYPE = "com.laixer.swabbr"
    }
}
