package com.laixer.swabbr.presentation.auth.login

import android.Manifest
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.firebase.iid.FirebaseInstanceId
import com.laixer.cache.Cache
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.auth.UserManager.Companion.KEY_ACCOUNT_NAME
import com.laixer.swabbr.presentation.model.UserCompleteItem
import kotlinx.android.synthetic.main.activity_app.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 *  Fragment for logging the user in. This will also ask the required
 *  permissions to use the app. On decline, we can't log the user in.
 */
class LoginFragment : Fragment() {
    private val vm: AuthViewModel by sharedViewModel()
    private val cache: Cache by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectFeature()

        vm.authenticatedUser.observe(viewLifecycleOwner, Observer(this@LoginFragment::login))

        addListeners()

        emailInput.setText(cache.get<String>(KEY_ACCOUNT_NAME) ?: "")

        loginButton.setOnClickListener { onClickLogin() }
        registerButton.setOnClickListener { onClickRegister() }
    }

    /**
     *  Asks for permissions, then logs the user in. Note that if
     *  any permissions are declined, the user won't be logged in.
     */
    private fun onClickLogin() {
        askPermission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO) {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                require(task.isSuccessful) { "Unable to identify this device on Firebase" }
                vm.login(
                    emailInput.text.toString(),
                    passwordInput.text.toString(),
                    task.result!!.token
                )
            }
        }.onDeclined {
            showMessage("Your permissions are required to use the app")
        }
    }

    /**
     *  Takes us to the registration fragment.
     */
    private fun onClickRegister() {
        findNavController().navigate(
            LoginFragmentDirections.actionRegister(), FragmentNavigatorExtras(
                emailInput to "emailInput"
            )
        )
    }

    private fun login(res: Resource<UserCompleteItem?>) {
        when (res.state) {
            ResourceState.LOADING -> {
                loading_icon_login.visible()
            }
            ResourceState.SUCCESS -> {
                loading_icon_login.gone()
            }
            ResourceState.ERROR -> {
                loading_icon_login.gone()

                passwordInput.text.clear()
                Log.e(TAG, res.message!!)
                Toast.makeText(requireActivity().applicationContext, res.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun addListeners() {
        val watcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                return
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                checkChanges()
            }

            override fun afterTextChanged(editable: Editable) {
                return
            }
        }

        emailInput.addTextChangedListener(watcher)
        passwordInput.addTextChangedListener(watcher)
    }

    private fun checkChanges() {
        loginButton.isEnabled = !(emailInput.text.isNullOrEmpty() || passwordInput.text.isNullOrEmpty())
    }


    companion object {
        private const val TAG = "LoginFragment"
    }
}
