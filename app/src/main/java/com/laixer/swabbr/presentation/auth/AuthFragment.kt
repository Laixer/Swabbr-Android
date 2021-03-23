package com.laixer.swabbr.presentation.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

/**
 *  Fragment which requires the user to be logged in. Whenever this
 *  fragment is entered, the user authentication is checked. If no
 *  valid authentication is discovered, we are redirected to the
 *  login page.
 */
abstract class AuthFragment : Fragment() {
    protected val authVm: AuthViewModel by sharedViewModel()

    /**
     *  Attaches an error handler to our API calls whenever a
     *  401 is returned. This would imply that we are no longer
     *  authenticated and will act on that.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectFeature()

        // Check for authentication at the first possible moment.
        checkIfAuthenticated()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the authentication fail resource in the vm.
        authVm.getNewAuthenticationRequiredResource()
            .observe(viewLifecycleOwner, Observer { onShouldRedirectToLogin(it) })
    }

    /**
     *  TODO When is this called again?
     */
    override fun onResume() {
        super.onResume()

        checkIfAuthenticated()
    }

    /**
     *  Navigates to the login fragment if we aren't authenticated.
     */
    private fun checkIfAuthenticated() {
        if (!authVm.isAuthenticated()) {
            findNavController().navigate(R.id.action_global_loginFragment)
        }
    }

    // TODO Is this the right solution? Probably not...
    /**
     *  Gets the id of the current user. This should always be safe to
     *  call as the AuthFragment should never not have an authenticated
     *  user. This might introduce possible race conditions, so if we
     *  can't get the id this will return a random value and simulate
     *  a back press.
     */
    protected fun getSelfId(): UUID {
        val id = authVm.getSelfIdOrNull()
        return if (id == null) {
            findNavController().popBackStack()
            UUID.randomUUID()
        } else {
            id
        }
    }

    /**
     *  If our [authVm] determines we can't stay authenticated for
     *  whatever reason, redirect the user to the login page.
     */
    private fun onShouldRedirectToLogin(res: Resource<Boolean>) {
        when (res.state) {
            ResourceState.LOADING -> {
            }
            ResourceState.SUCCESS -> { // If this resource is true we require new authentication by the user.
                res.data?.let {
                    if (it) {
                        findNavController().navigate(R.id.action_global_loginFragment)
                    }
                }
            }
            ResourceState.ERROR -> {
            }
        }
    }


    companion object {
        const val TAG = "AuthActivity"
    }
}
