package com.laixer.swabbr.presentation.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.laixer.navigation.features.SwabbrNavigation
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.injectFeature
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {
    private val vm: LoginViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addListeners()

        loginButton.setOnClickListener {
            vm.login(Login(usernameInput.text.toString(), passwordInput.text.toString(), rememberMeSwitch.isChecked))
        }

        registerButton.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionRegister())
        }

        injectFeature()

        vm.authorized.observe(viewLifecycleOwner, Observer { login(it) })
    }

    private fun login(res: Resource<Boolean>) {
        when (res.state) {
            ResourceState.LOADING -> {
                progressBar.visible()
            }
            ResourceState.SUCCESS -> {
                progressBar.gone()
                startActivity(SwabbrNavigation.vlogList())
            }
            ResourceState.ERROR -> {
                passwordInput.text.clear()
                progressBar.gone()
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

        usernameInput.addTextChangedListener(watcher)
        passwordInput.addTextChangedListener(watcher)
    }

    private fun checkChanges() {
        loginButton.isEnabled = !(usernameInput.text.isNullOrEmpty() || passwordInput.text.isNullOrEmpty())
    }
}
