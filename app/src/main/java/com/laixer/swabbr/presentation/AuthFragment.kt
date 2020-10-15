package com.laixer.swabbr.presentation

import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.laixer.presentation.Resource
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.auth.UserManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

/** Fragment that forces authentication on create, view creation and resume **/
open class AuthFragment : Fragment() {

    private val am: AccountManager by inject()
    private val authVm: AuthViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        injectFeature()
        super.onCreate(savedInstanceState)
    }

    protected fun onError(resource: Resource<Any?>) {
        if (resource.message?.contains("401") == true) {
            reauthorize()
        }
    }

    protected fun getAuthToken(): String {
        return am.peekAuthToken(authVm.get(), UserManager.DEFAULT_AUTH_TOKEN_TYPE)
    }

    protected fun getAuthUserId(): UUID {
        return UUID.fromString(am.getUserData(authVm.get(), "id"))
    }

    private fun reauthorize(message: String? = "Please sign in.") =
        findNavController().navigate(
            R.id.action_global_authenticator,
            null,
            NavOptions.Builder().build()
        ).also {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }

    companion object {
        private val ACCOUNT_TYPE = "com.laixer.swabbr"
    }
}
