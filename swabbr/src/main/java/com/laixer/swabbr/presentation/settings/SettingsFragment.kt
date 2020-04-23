package com.laixer.swabbr.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.model.FollowMode
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.model.SettingsItem
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val vm: SettingsViewModel by viewModel()
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
                vm.setSettings(it)
            }
        }

        logout.setOnClickListener {
            vm.logout()
        }

        injectFeature()

        if (savedInstanceState == null) {
            vm.getSettings(false)
        }

        vm.settings.observe(viewLifecycleOwner, Observer { loadSettings(it) })
        vm.logout.observe(viewLifecycleOwner, Observer { logout(it) })
    }

    private fun loadSettings(res: Resource<SettingsItem?>) {
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
                }
            }
            ResourceState.ERROR -> {
                Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT).show()
                enableSettings(true)
            }
        }
    }

    private fun logout(res: Resource<String?>) {
        when (res.state) {
            ResourceState.LOADING -> {
                enableSettings(false)
                progressBar.visible()
            }
            ResourceState.SUCCESS -> {
                progressBar.gone()
//                startActivity(SwabbrNavigation.login())
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

    private fun enableSettings(enable: Boolean) {
        dailyVlogRequestLimitSpinner.isEnabled = enable
        followmodeSpinner.isEnabled = enable
        privateSwitch.isEnabled = enable
        logout.isEnabled = enable
    }

    private fun checkChanges() {
        saveSettings.isEnabled = settings != savedSettings
    }
}
