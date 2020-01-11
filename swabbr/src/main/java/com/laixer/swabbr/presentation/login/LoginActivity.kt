package com.laixer.swabbr.presentation.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.Observer
import com.laixer.cache.ReactiveCache
import com.laixer.navigation.features.SwabbrNavigation
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.datasource.cache.AuthCacheDataSourceImpl
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.injectFeature
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.progressBar
import kotlinx.android.synthetic.main.activity_vlog_details.*
import org.koin.androidx.viewmodel.ext.viewModel

class LoginActivity : AppCompatActivity() {
    private val vm: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        addListeners()

        loginButton.setOnClickListener {
            vm.login(Login(usernameInput.text.toString(), passwordInput.text.toString(), rememberMeSwitch.isChecked))
        }

        registerButton.setOnClickListener {
            startActivity(SwabbrNavigation.registration())
        }

        injectFeature()

        vm.authorized.observe(this, Observer { login(it) })
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
                Toast.makeText(this, res.message, Toast.LENGTH_SHORT).show()
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
