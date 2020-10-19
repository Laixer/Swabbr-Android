package com.laixer.swabbr.presentation.profile.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import com.auth0.android.jwt.JWT
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.model.FollowMode
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.model.AuthUserItem
import com.laixer.swabbr.presentation.model.SettingsItem
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SettingsFragment : AuthFragment() {
    private val vm: SettingsViewModel by sharedViewModel()
    private val authVm: AuthViewModel by sharedViewModel()

    private var settings: SettingsItem? = null
    private var savedSettings: SettingsItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectFeature()
        prepareUI()

        saveSettings.setOnClickListener {
            settings?.let {
                enableSettings(false)
                vm.setSettings(it)
            }
        }

        logout.setOnClickListener {
            enableSettings(false)
            authVm.logout()
        }

        vm.settings.observe(viewLifecycleOwner, Observer { loadSettings(it) })
        authVm.authenticatedUser.observe(viewLifecycleOwner, Observer { logout(it) })

        vm.getSettings(true)
    }

    private fun loadSettings(res: Resource<SettingsItem>) {
        when (res.state) {
            ResourceState.LOADING -> {
                enableSettings(false)
            }
            ResourceState.SUCCESS -> {
                settings = res.data
                settings?.let {
                    savedSettings = it.copy()

                    privateSwitch.isChecked = it.private
                    dailyVlogRequestLimitSpinner.setSelection(it.dailyVlogRequestLimit)
                    followmodeSpinner.setSelection(it.followMode.ordinal)
                    enableSettings(true)
                    checkChanges()
                }
            }
            ResourceState.ERROR -> {
                Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT).show()
                enableSettings(true)
                checkChanges()
            }
        }
    }

    private fun logout(res: Resource<AuthUserItem?>) {
        when (res.state) {
            ResourceState.LOADING -> {
                enableSettings(false)
                progressBar.visible()
            }
            ResourceState.SUCCESS -> {
                progressBar.gone()
            }
            ResourceState.ERROR -> {
                Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT).show()
                progressBar.gone()
                enableSettings(true)
            }
        }
    }

    private fun prepareUI() {
        prepareSwitch()
        prepareSpinners()
    }

    private fun prepareSwitch() {
        privateSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings?.private = isChecked
            checkChanges()
        }
    }

    private fun prepareSpinners() {
        dailyVlogRequestLimitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                settings?.dailyVlogRequestLimit = position
                checkChanges()
            }
        }

        followmodeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                settings?.followMode = FollowMode.values().first { it.ordinal == position }
                checkChanges()
            }
        }

        ArrayAdapter.createFromResource(
            requireContext(), R.array.dailyvlogrequestlimit_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dailyVlogRequestLimitSpinner.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            requireContext(), R.array.followmode_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            followmodeSpinner.adapter = adapter
        }
    }

    private fun enableSettings(enable: Boolean) = with(enable) {
        dailyVlogRequestLimitSpinner.isEnabled = this
        followmodeSpinner.isEnabled = this
        privateSwitch.isEnabled = this
        logout.isEnabled = this
    }

    private fun checkChanges() {
        saveSettings.isEnabled = settings != savedSettings
    }
}
