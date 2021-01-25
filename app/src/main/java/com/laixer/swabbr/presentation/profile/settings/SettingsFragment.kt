package com.laixer.swabbr.presentation.profile.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.types.FollowMode
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.model.UserCompleteItem
import com.laixer.swabbr.presentation.model.UserUpdatablePropertiesItem
import com.laixer.swabbr.presentation.model.copy
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

// TODO This contains some race conditions. Updates sent to the backend
//      don't show until refreshed.
/**
 *  Fragment for displaying and updating user settings.
 */
class SettingsFragment : AuthFragment() {
    private val vm: SettingsViewModel by sharedViewModel()
    private val authVm: AuthViewModel by sharedViewModel()

    private var userProperties: UserUpdatablePropertiesItem? = null
    private var userPropertiesSaved: UserUpdatablePropertiesItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    /**
     *  Adds onClickListeners and binds change functions to
     *  observable view model resources.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectFeature()
        prepareUI()

        saveSettings.setOnClickListener {
            userProperties?.let {
                enableSettings(false)
                vm.setUpdatableProperties(it)
            }
        }

        logout.setOnClickListener {
            enableSettings(false)
            authVm.logout()
        }

        // TODO Question Why is this called in OnViewCreated, while
        //      other fragments have these functions in OnCreateView?
        vm.settings.observe(viewLifecycleOwner, Observer { loadSettingsFromResource(it) })
        authVm.authenticatedUser.observe(viewLifecycleOwner, Observer { logout(it) })

        vm.getUpdatableProperties(true)
    }

    /**
     *  Called when the observed [vm] resource changes.
     *
     *  @param res The observed resource.
     */
    private fun loadSettingsFromResource(res: Resource<UserUpdatablePropertiesItem>) {
        when (res.state) {
            ResourceState.LOADING -> {
                enableSettings(false)
            }
            ResourceState.SUCCESS -> {
                userProperties = res.data
                userProperties?.let {
                    userPropertiesSaved = it.copy()

                    privateSwitch.isChecked = it.isPrivate!!
                    dailyVlogRequestLimitSpinner.setSelection(it.dailyVlogRequestLimit!!)
                    followmodeSpinner.setSelection(it.followMode?.ordinal!!)
                    enableSettings(true)
                    checkChanges()
                }
            }
            ResourceState.ERROR -> {
                enableSettings(true)
                checkChanges()
            }
        }
    }

    // TODO Why do we need this?
    /**
     *  Logout function which is called when the observed
     *  user resource changes.
     *
     *  @param res The observed user resource.
     */
    private fun logout(res: Resource<UserCompleteItem?>) {
        when (res.state) {
            ResourceState.LOADING -> {
                enableSettings(false)
                progressBar.visible()
            }
            ResourceState.SUCCESS -> {
                progressBar.gone()
            }
            ResourceState.ERROR -> {
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
            userProperties?.isPrivate = isChecked
            checkChanges()
        }
    }

    private fun prepareSpinners() {
        dailyVlogRequestLimitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                userProperties?.dailyVlogRequestLimit = position
                checkChanges()
            }
        }

        followmodeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                userProperties?.followMode = FollowMode.values().first { it.ordinal == position }
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
        saveSettings.isEnabled = userProperties != userPropertiesSaved
    }
}
