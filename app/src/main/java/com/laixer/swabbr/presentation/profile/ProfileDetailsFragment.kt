package com.laixer.swabbr.presentation.profile

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.types.FollowMode
import com.laixer.swabbr.domain.types.Gender
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.auth.AuthFragment
import com.laixer.swabbr.presentation.model.UserCompleteItem
import com.laixer.swabbr.presentation.model.UserUpdatablePropertiesItem
import com.laixer.swabbr.presentation.model.extractUpdatableProperties
import com.laixer.swabbr.presentation.model.extractUser
import com.laixer.swabbr.presentation.utils.onActivityResult
import com.laixer.swabbr.presentation.utils.selectProfileImage
import com.laixer.swabbr.utils.loadAvatarFromUser
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import kotlinx.android.synthetic.main.fragment_profile_details.*
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.fragment_registration.fab_set_profile_image
import kotlinx.android.synthetic.main.fragment_registration.inputNickname
import java.io.File
import java.util.*
import kotlin.math.min

// TODO This doesn't allow us to set any properties to null. Maybe we want this for first name and last name?
/**
 *  Fragment for displaying profile details of the current user
 *  which includes update functionality for all properties displayed.
 *  Getting the initial vm data is done by [ProfileFragment].
 *
 *  Note that the follow mode is controlled by [switchIsPrivate].
 *
 *  @param userId The id of the profile we are looking at. Note that at
 *                the moment this will always be the current user. This
 *                might change in the future.
 *  @param profileVm Single profile vm instance from [ProfileFragment].
 */
class ProfileDetailsFragment(
    private val userId: UUID,
    private val profileVm: ProfileViewModel
) : AuthFragment() {

    /**
     *  Flag set by [confirmChanges] when we are awaiting a data
     *  save. When [onUserCompleteUpdated] is called and this flag
     *  is set to true, a message confirming our changes is displayed.
     */
    private var isSaving: Boolean = false

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
     *  Attaches [onUserCompleteUpdated] to the [authVm]
     *  observable user resource.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        profileVm.selfComplete.observe(viewLifecycleOwner, Observer { onUserCompleteUpdated(it) })

        return inflater.inflate(R.layout.fragment_profile_details, container, false)
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
        fab_set_profile_image.isEnabled = false // Only enable after init
        fab_set_profile_image.setOnClickListener { ImagePicker.selectProfileImage(this) }

        // Get a date picker popup for modifying our birth date.
        inputBirthDate.setupClickListener(
            manager = requireActivity().supportFragmentManager,
            title = requireContext().getString(R.string.dialog_date_picker_birth_date),
            callback = {
                if (!::userOriginal.isInitialized) return@setupClickListener // TODO What happens here?
                userUpdatableProperties.birthDate = it
            })

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
                    if (!::userOriginal.isInitialized) {
                        return
                    }

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
                    if (!::userOriginal.isInitialized) {
                        return
                    }

                    // TODO This is bad design, no guarantee of position matching desired value.
                    userUpdatableProperties.dailyVlogRequestLimit = position
                }
            }

        button_profile_details_save.setOnClickListener { confirmChanges() }
        button_profile_logout.setOnClickListener {
            // This triggers the user manager which will update its resources.
            // The auth fragment we inherit from will take us back to login.
            // TODO It doesn't because Android can't make a consistent navcontroller. This is ridiculous.
            //      The nav to login only works ONCE, then never again.
             authVm.logout(requireContext())

            // TODO This is the fix. Beautiful.
            requireActivity().finish()
        }

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

        // Setup interests
        inputInterest1.doOnTextChanged { text, _, _, _ ->
            if (!::userOriginal.isInitialized) return@doOnTextChanged
            if (text?.isNotBlank() == true) {
                userUpdatableProperties.interest1 = text.toString()
            }
        }
        inputInterest2.doOnTextChanged { text, _, _, _ ->
            if (!::userOriginal.isInitialized) return@doOnTextChanged
            if (text?.isNotBlank() == true) {
                userUpdatableProperties.interest2 = text.toString()
            }
        }
        inputInterest3.doOnTextChanged { text, _, _, _ ->
            if (!::userOriginal.isInitialized) return@doOnTextChanged
            if (text?.isNotBlank() == true) {
                userUpdatableProperties.interest3 = text.toString()
            }
        }

        // TODO
        // Delete account button

        // Report abuse button
        text_clickable_profile_details_report_abuse.setOnClickListener {
            // TODO Move to some config file, even though this is probably temporary.
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://swabbr.com/abuse"))
            requireActivity().startActivity(browserIntent)
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
     */
    private fun onProfileImageSelected(imageFile: File, imageBitmap: Bitmap) {
        user_profile_profile_image_insettings.setImageBitmap(imageBitmap)
        userUpdatableProperties.profileImageFile = imageFile
    }

    /**
     *  This updates the form values based on a given user object.
     *
     *  @param user The user to extract properties from.
     */
    private fun setFormValues(user: UserCompleteItem) {
        // Enable again
        fab_set_profile_image.isEnabled = true

        user_profile_profile_image_insettings.loadAvatarFromUser(user.extractUser())
        inputBirthDate.setDate(user.birthDate)
        inputNickname.setText(user.nickname)
        inputFirstName.setText(user.firstName)
        inputLastName.setText(user.lastName)
        switchIsPrivate.isChecked = user.isPrivate
        spinnerGender.setSelection(min(Gender.values().size, user.gender.ordinal))
        spinnerDailyVlogRequestLimit.setSelection(min(3, user.dailyVlogRequestLimit)) // TODO Hard coded limit
        inputInterest1.setText(user.interest1)
        inputInterest2.setText(user.interest2)
        inputInterest3.setText(user.interest3)
    }

    /**
     *  Called when we confirm our changes in the UI.
     */
    private fun confirmChanges() {
        isSaving = true

        profileVm.updateGetSelf(userUpdatableProperties)
    }

    /**
     *  This function is attached to the observable user object
     *  in the [authVm]. Whenever the current user object in
     *  [authVm] changes, this function gets called.
     *
     *  Note that this function does not actually perform any user
     *  updating operation. It just syncs the UI with [authVm].
     *
     *  @param res The user resource.
     */
    private fun onUserCompleteUpdated(res: Resource<UserCompleteItem>) {
        when (res.state) {
            ResourceState.LOADING -> {
            }
            ResourceState.SUCCESS -> {
                res.data?.let { user ->
                    /** First the updatable properties, then the other. This is because
                    we check the userOriginal during calls in [onViewCreated]. */
                    userUpdatableProperties = user.extractUpdatableProperties()
                    userOriginal = user.copy()
                    setFormValues(user)
                }

                if (isSaving) {
                    showMessage("Changes saved")
                    isSaving = false
                }
            }
            ResourceState.ERROR -> {
                showMessage("Couldn't get user profile settings")
            }
        }
    }

    internal companion object {
        const val TAG = "AuthProfileFragment"
    }
}
