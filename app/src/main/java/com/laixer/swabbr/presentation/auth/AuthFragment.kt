package com.laixer.swabbr.presentation.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.MainActivity
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
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
     *  Modify this parameter to determine the refresh parameter
     *  for the first [getData] call. Defaults to false.
     */
    protected var defaultRefresh: Boolean = false

    /**
     *  Flag to help us trigger [getData] in [onResume].
     */
    private var wasRedirectedToLogin: Boolean = false

    /**
     *  Attaches an error handler to our API calls whenever a
     *  401 is returned. This would imply that we are no longer
     *  authenticated and will act on that.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectFeature()

        // Check for authentication at the first possible moment.
        if (authVm.isAuthenticated()) {
            getData()
        } else {
            (requireActivity() as MainActivity?)?.tryRedirectToLogin()
            wasRedirectedToLogin = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the authentication fail resource in the vm.
        authVm.getNewAuthenticationRequiredResource()
            .observe(viewLifecycleOwner, Observer { onShouldRedirectToLogin(it) })
    }

    /**
     *  Override this method to only get data if we are authenticated.
     */
    protected open fun getData(refresh: Boolean = defaultRefresh) {}

    // TODO Is this the right solution? Probably not...
    /**
     *  Gets the id of the current user. This should always be safe to
     *  call as the AuthFragment should never not have an authenticated
     *  user. This might introduce possible race conditions, so if we
     *  can't get the id this will return a random value and simulate
     *  a back press.
     */
    protected fun getSelfId(): UUID = authVm.getSelfIdOrNull() ?: UUID.randomUUID()

    /**
     *  If our [authVm] determines we can't stay authenticated for
     *  whatever reason, notify [MainActivity] and tell it to try
     *  to redirect us to the login page.
     */
    private fun onShouldRedirectToLogin(res: Resource<Boolean>) {
        when (res.state) {
            ResourceState.LOADING -> {
            }
            ResourceState.SUCCESS -> { // If this resource is true we require new authentication by the user.
                res.data?.let { newAuthenticationRequired ->
                    if (newAuthenticationRequired) {
                        // Notify our activity that we should be redirected.
                        (requireActivity() as MainActivity?)?.tryRedirectToLogin()
                        wasRedirectedToLogin = true
                    } else {
                        // Notify our activity that we have logged back in.
                        (requireActivity() as MainActivity?)?.hasLoggedIn()
                    }
                }
            }
            ResourceState.ERROR -> {
            }
        }
    }

    /**
     *  Re-trigger [getData] if we were redirected before.
     */
    override fun onResume() {
        super.onResume()

        // Only get data if we are authenticated.
        if (wasRedirectedToLogin && authVm.isAuthenticated()) {
            wasRedirectedToLogin = false
            getData()
        }
    }

    companion object {
        const val TAG = "AuthActivity"
    }
}
