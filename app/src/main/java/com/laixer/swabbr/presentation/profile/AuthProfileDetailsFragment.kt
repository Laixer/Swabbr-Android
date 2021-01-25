package com.laixer.swabbr.presentation.profile

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.UserCompleteItem
import kotlinx.android.synthetic.main.fragment_auth_profile_details.*

// TODO The user updates should be done using [UserUpdatablePropertiesItem].
/**
 *  Fragment for displaying profile details of the current user.
 *  This can also update user properties. Note that some other
 *  user settings are updated in [SettingsFragment].
 */
class AuthProfileDetailsFragment : AuthFragment() {

    private lateinit var userOriginal: UserCompleteItem
    private lateinit var userCopy: UserCompleteItem

    /**
     *  Attaches [updatePropertiesFromViewModel] to the [authUserVm]
     *  observable user resource.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        authUserVm.user.observe(viewLifecycleOwner, Observer { updatePropertiesFromViewModel(it) })
        return inflater.inflate(R.layout.fragment_auth_profile_details, container, false)
    }

    /**
     *  This attaches all change listeners for this fragment.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkChanges()

        firstNameInput.doOnTextChanged { text, _, _, _ ->
            if (!::userCopy.isInitialized) return@doOnTextChanged

            userCopy = userCopy.apply {
                firstName = if (text?.isNotBlank() == true) text.toString() else null
            }
            checkChanges()
        }

        lastNameInput.doOnTextChanged { text, _, _, _ ->
            if (!::userCopy.isInitialized) return@doOnTextChanged

            userCopy = userCopy.apply {
                lastName = if (text?.isNotBlank() == true) text.toString() else null
            }
            checkChanges()
        }

        nicknameInput.doOnTextChanged { text, _, _, _ ->
            if (!::userCopy.isInitialized) return@doOnTextChanged

            userCopy = userCopy.apply {
                nickname = if (text?.isNotBlank() == true) text.toString() else ""
            }
            checkChanges()
        }

        privateSwitch.setOnCheckedChangeListener { _, _ ->
            if (!::userCopy.isInitialized) return@setOnCheckedChangeListener

            userCopy = userCopy.apply {
                isPrivate = privateSwitch.isChecked
            }
            checkChanges()
        }

        applyButton.setOnClickListener {
            if (!::userCopy.isInitialized) return@setOnClickListener

            applyButton.isEnabled = false
            authUserVm.updateSelf(userCopy)
        }
    }

    /**
     *  If any properties have been modified in the form, set
     *  the [applyButton] to enabled. Else, set it to disabled.
     *  If the [userOriginal] has not yet been initialized, the
     *  [applyButton] will be disabled as well.
     */
    private fun checkChanges() {
        applyButton.isEnabled =
            if (!::userOriginal.isInitialized) {
                false
            } else {
                userOriginal.equals(userCopy).not()
            }
    }

    /**
     *  This updates the form values based on a given user object.
     *
     *  @param user The user to extract properties from.
     */
    fun setFormValues(user: UserCompleteItem) {
        firstNameInput.setText(user.firstName)
        lastNameInput.setText(user.lastName)
        nicknameInput.setText(user.nickname)
        privateSwitch.isChecked = user.isPrivate

        checkChanges()
    }

    // TODO Merge with UserUpdatablePropertiesItem
    /**
     *  This function is attached to the observable user object
     *  in the [authUserVm]. Whenever the current user object in
     *  [authUserVm] changes, this function gets called.
     *
     *  Note that this function does not actually perform any user
     *  updating operation. It just syncs the UI with [authUserVm].
     *
     *  @param res The user resource.
     */
    private fun updatePropertiesFromViewModel(res: Resource<UserCompleteItem>) {
        when (res.state) {
            ResourceState.LOADING -> {
                checkChanges()
                // TODO: Loading state
            }
            ResourceState.SUCCESS -> {
                res.data?.let { user ->
                    userOriginal = user.copy()
                    userCopy = user.copy()
                    setFormValues(user)
                }
            }
            ResourceState.ERROR -> {
                checkChanges()
                Toast.makeText(requireActivity(), res.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    internal companion object {
        const val TAG = "AuthProfileFragment"
    }
}
