package com.laixer.swabbr.presentation.auth.login

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
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : Fragment() {
    private val vm: AuthViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectFeature()

        addListeners()

        loginButton.setOnClickListener {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                require(task.isSuccessful) { "Unable to identify this device on Firebase" }
                vm.login(
                    LoginItem(
                        emailInput.text.toString(),
                        passwordInput.text.toString(),
                        true,
                        PushNotificationPlatform.FCM,
                        task.result!!.token
                    ),
                    true
                )
            }
        }

        registerButton.setOnClickListener {
            val extras = FragmentNavigatorExtras(
                emailInput to "emailInput"
            )
            findNavController().navigate(LoginFragmentDirections.actionRegister(), extras)
        }

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
                    activity?.finish()
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
