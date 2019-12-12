package com.laixer.swabbr.presentation.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.model.SettingsItem
import kotlinx.android.synthetic.main.activity_settings.*
import org.koin.androidx.viewmodel.ext.viewModel

class SettingsActivity : AppCompatActivity() {
    private val vm: SettingsViewModel by viewModel()

    private var settings: SettingsItem? = null
    private var savedSettings: SettingsItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        loadSpinners()

        saveSettings.setOnClickListener {
            settings?.let {
                vm.setSettings(it)
            }
        }

        injectFeature()

        if (savedInstanceState == null) {
            vm.getSettings(false)
        }

        vm.settings.observe(this, Observer { loadSettings(it) })
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
                    followmodeSpinner.setSelection(it.followMode)

                    enableSettings(true)
                }
            }
            ResourceState.ERROR -> {
                Toast.makeText(this, res.message, Toast.LENGTH_SHORT).show()
                enableSettings(true)
            }
        }
    }

    private fun loadSpinners() {
        privateSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings?.private = isChecked
            checkChanges()
        }

        dailyVlogRequestLimitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { return }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                settings?.dailyVlogRequestLimit = position
                checkChanges()
            }
        }

        followmodeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { return }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                settings?.followMode = position
                checkChanges()
            }
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.dailyvlogrequestlimit_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dailyVlogRequestLimitSpinner.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.followmode_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            followmodeSpinner.adapter = adapter
        }
    }

    private fun enableSettings(enable: Boolean) {
        dailyVlogRequestLimitSpinner.isEnabled = enable
        followmodeSpinner.isEnabled = enable
        privateSwitch.isEnabled = enable
    }

    private fun checkChanges() { saveSettings.isEnabled = settings != savedSettings }
}