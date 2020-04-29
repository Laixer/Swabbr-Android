package com.laixer.swabbr.presentation.login

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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.iid.FirebaseInstanceId
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.model.PushNotificationPlatform
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.model.AuthUserItem
import com.laixer.swabbr.presentation.model.LoginItem
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {
    private val vm: AuthViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addListeners()

        loginButton.setOnClickListener {
            vm.login(
                LoginItem(
                    usernameInput.text.toString(),
                    passwordInput.text.toString(),
                    rememberMeSwitch.isChecked,
                    PushNotificationPlatform.FCM,
                    FirebaseInstanceId.getInstance().id
                ),
                rememberMeSwitch.isChecked
            )
        }

        registerButton.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionRegister())
        }

        injectFeature()

        vm.authenticatedUser.observe(viewLifecycleOwner, Observer { login(it) })
    }

    private fun login(res: Resource<AuthUserItem?>) {
        when (res.state) {
            ResourceState.LOADING -> {
                progressBar.visible()
            }
            ResourceState.SUCCESS -> {
                res.data?.let {
                    progressBar.gone()
                    proceed()
                } ?: run {
                    progressBar.gone()
                    Log.e(TAG, res.message!!)
                    Toast.makeText(requireActivity().applicationContext, res.message, Toast.LENGTH_SHORT).show()
                }

            }
            ResourceState.ERROR -> {
                passwordInput.text.clear()
                progressBar.gone()
                Log.e(TAG, res.message!!)
                Toast.makeText(requireActivity().applicationContext, res.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun proceed() = Navigation.findNavController(requireView()).navigate(R.id.mainActivity)

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

        usernameInput.addTextChangedListener(watcher)
        passwordInput.addTextChangedListener(watcher)
    }

    private fun checkChanges() {
        loginButton.isEnabled = !(usernameInput.text.isNullOrEmpty() || passwordInput.text.isNullOrEmpty())
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}
