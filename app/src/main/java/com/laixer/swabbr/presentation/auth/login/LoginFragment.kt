package com.laixer.swabbr.presentation.auth.login

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import com.laixer.swabbr.presentation.utils.todosortme.gone
import com.laixer.swabbr.presentation.utils.todosortme.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.hideSoftKeyboard
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.auth.AuthFragment
import com.laixer.swabbr.presentation.auth.AuthViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 *  Fragment for logging the user in. This will also ask the required
 *  permissions to use the app. On decline, we can't log the user in.
 */
class LoginFragment : Fragment() {

    private val authVm: AuthViewModel by sharedViewModel()

    /**
     *  Inflate the view.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    /**
     *  Setup the UI.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectFeature()

        authVm.authenticationResultResource.observe(viewLifecycleOwner, Observer { onAuthenticationResult(it) })

        // If we have the username in our cache, already fill that in.
        emailInput.setText(authVm.getCachedEmailOrNull() ?: "")

        emailInput.addTextChangedListener { checkChanges() }
        passwordInput.addTextChangedListener { checkChanges() }

        loginButton.setOnClickListener { onClickLogin() }
        registerButton.setOnClickListener { onClickRegister() }
    }

    /**
     *  Asks for permissions, then logs the user in. Note that if
     *  any permissions are declined, the user won't be logged in.
     */
    private fun onClickLogin() {
        // Explicitly hide the input keyboard as Android doesn't do this for us.
        hideSoftKeyboard()

        askPermission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO) {
            authVm.login(
                emailInput.text.toString(),
                passwordInput.text.toString()
            )
        }.onDeclined {
            showMessage("Your permissions are required to use the app")
        }
    }

    /**
     *  Takes us to the registration fragment.
     */
    private fun onClickRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
    }

    /**
     *  Called when a login (or registration) operation completes.
     */
    private fun onAuthenticationResult(res: Resource<Boolean>) {
        when (res.state) {
            ResourceState.LOADING -> {
                loading_icon_login.visible()
            }
            ResourceState.SUCCESS -> {
                loading_icon_login.gone()

                if (res.data == true) {
                    // Go back, as this will always be put on top of the original app stack.
                    // Look at the nav graph for further understanding of how this works,
                    // along with the AuthFragment class.
                    findNavController().popBackStack()
                }
                // No else case is required, as a failure to login/register will
                // result in the resource error state. TODO Kind of suboptimal.
            }
            ResourceState.ERROR -> {
                loading_icon_login.gone()

                passwordInput.text.clear()
                Log.e(TAG, res.message!!)

                showMessage("Could not login")
            }
        }
    }

    /**
     *  Called whenever the [emailInput] or [passwordInput] input text changes.
     *  When both of these fields have some
     */
    private fun checkChanges() {
        loginButton.isEnabled = !(emailInput.text.isNullOrEmpty() || passwordInput.text.isNullOrEmpty())
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}
