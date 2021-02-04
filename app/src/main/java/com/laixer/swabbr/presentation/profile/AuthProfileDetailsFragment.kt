package com.laixer.swabbr.presentation.profile

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.github.dhaval2404.imagepicker.ImagePicker
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.types.FollowMode
import com.laixer.swabbr.domain.types.Gender
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.model.UserCompleteItem
import com.laixer.swabbr.presentation.model.UserUpdatablePropertiesItem
import com.laixer.swabbr.presentation.model.extractUpdatableProperties
import com.laixer.swabbr.presentation.utils.onActivityResult
import com.laixer.swabbr.presentation.utils.selectProfileImage
import com.laixer.swabbr.utils.encodeToBase64
import com.laixer.swabbr.utils.loadAvatar
import kotlinx.android.synthetic.main.fragment_auth_profile_details.*
import kotlinx.android.synthetic.main.fragment_auth_profile_details.fab_set_profile_image
import kotlinx.android.synthetic.main.fragment_auth_profile_details.inputNickname
import kotlinx.android.synthetic.main.fragment_registration.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.LocalDate

// TODO This doesn't allow us to set any properties to null. Maybe we want this for first name and last name?
/**
 *  Fragment for displaying profile details of the current user
 *  which includes update functionality for all properties displayed.
 *
 *  Note that this makes an API call each time we modify a value.
 *  This might be suboptimal TODO Fix.
 *
 *  Note that the follow mode is controller by [switchIsPrivate].
 */
class AuthProfileDetailsFragment: AuthFragment() {
    private val authVm: AuthViewModel by sharedViewModel()

    /**
     *  Used as a flag to prevent UI updates before the user has
     *  been fetched, and as a reference for [userUpdatableProperties].
     */
    private lateinit var userOriginal: UserCompleteItem

    /**
     *  Object which we modify. If we confirm, this object will
     *  be sent to the backend for the user update procedure.
     */
    private lateinit var userUpdatableProperties: UserUpdatablePropertiesItem

    /**
     *  Attaches [updatePropertiesFromViewModel] to the [authUserVm]
     *  observable user resource.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        authUserVm.user.observe(viewLifecycleOwner, Observer { updatePropertiesFromViewModel(it) })
        return inflater.inflate(R.layout.fragment_auth_profile_details, container, false)
    }

    /**
     *  This attaches all change listeners for this fragment. This
     *  function is quite lengthy as we have a lot of assignment to
     *  do. Note that the order of assignment matches the UI from
     *  top to bottom.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** This launches the ImagePicker activity and is resumed in [onActivityResult]. */
        fab_set_profile_image.setOnClickListener { ImagePicker.selectProfileImage(this) }

        inputBirthDate.doOnTextChanged { text, _, _, _ ->
            if (!::userOriginal.isInitialized) return@doOnTextChanged
            if (text?.isNotBlank() == true) {
                userUpdatableProperties.birthDate = LocalDate.parse(text.toString())
            }
        }

        inputNickname.doOnTextChanged { text, _, _, _ ->
            if (!::userOriginal.isInitialized) return@doOnTextChanged
            if (text?.isNotBlank() == true) {
                userUpdatableProperties.nickname = text.toString()
            }
        }

        inputFirstName.doOnTextChanged { text, _, _, _ ->
            if (!::userOriginal.isInitialized) return@doOnTextChanged
            if (text?.isNotBlank() == true) {
                userUpdatableProperties.firstName = text.toString()
            }
        }

        inputLastName.doOnTextChanged { text, _, _, _ ->
            if (!::userOriginal.isInitialized) return@doOnTextChanged
            if (text?.isNotBlank() == true) {
                userUpdatableProperties.lastName = text.toString()
            }
        }

        spinnerGender.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    return
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // TODO This is bad design, no guarantee of position matching desired value.
                    userUpdatableProperties.gender = Gender.values().first { it.ordinal == position }
                }
            }

        // Note that the isPrivate switch also controls the follow mode.
        switchIsPrivate.setOnCheckedChangeListener { _, _ ->
            if (!::userOriginal.isInitialized) return@setOnCheckedChangeListener
            userUpdatableProperties.isPrivate = switchIsPrivate.isChecked
            userUpdatableProperties.followMode =
                if (switchIsPrivate.isChecked) FollowMode.DECLINE_ALL else FollowMode.MANUAL
        }

        spinnerDailyVlogRequestLimit.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    return
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // TODO This is bad design, no guarantee of position matching desired value.
                    userUpdatableProperties.dailyVlogRequestLimit = position
                }
            }

        buttonConfirm.setOnClickListener { confirmChanges() }
        buttonLogout.setOnClickListener { authVm.logout() }

        // Setup spinner values
        ArrayAdapter.createFromResource(
            requireContext(), R.array.gender_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGender.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(), R.array.daily_vlog_request_limit_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDailyVlogRequestLimit.adapter = adapter
        }
    }

    /**
     *  Called by the [ImagePicker] activity. The result data contains the selected bitmap.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImagePicker.onActivityResult(
            context = this.requireContext(),
            resultCode = resultCode,
            data = data,
            successCallback = this::onProfileImageSelected
        )
    }

    /**
     *  Function that stores and sets our profile image if we select one.
     *  This
     *
     *  @param selectedBitmap The selected profile image.
     */
    private fun onProfileImageSelected(selectedBitmap: Bitmap) {
        user_profile_profile_image_insettings.setImageBitmap(selectedBitmap)
        userUpdatableProperties.profileImage = selectedBitmap.encodeToBase64()
    }

    /**
     *  This updates the form values based on a given user object.
     *
     *  @param user The user to extract properties from.
     */
    private fun setFormValues(user: UserCompleteItem) {
        user_profile_profile_image_insettings.loadAvatar(user.profileImage, user.id)
        //inputBirthDate.set TODO
        inputNickname.setText(user.nickname)
        inputFirstName.setText(user.firstName)
        inputLastName.setText(user.lastName)
        spinnerGender.setSelection(user.gender.ordinal)
        switchIsPrivate.isChecked = user.isPrivate
        spinnerDailyVlogRequestLimit.setSelection(user.dailyVlogRequestLimit)
    }

    /**
     *  Called when we confirm our changes in the UI.
     */
    private fun confirmChanges() {
        authUserVm.updateGetSelf(userUpdatableProperties)
    }

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
                // TODO: Loading state
            }
            ResourceState.SUCCESS -> {
                res.data?.let { user ->
                    userOriginal = user.copy()
                    userUpdatableProperties = user.extractUpdatableProperties() // TODO Not null assignment?
                    setFormValues(user)
                }
            }
            ResourceState.ERROR -> {
                Toast.makeText(requireActivity(), res.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    internal companion object {
        const val TAG = "AuthProfileFragment"
    }
}
