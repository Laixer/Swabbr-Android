package com.laixer.swabbr.presentation.auth.registration

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.types.PushNotificationPlatform
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.model.RegistrationItem
import com.laixer.swabbr.presentation.utils.onActivityResult
import com.laixer.swabbr.presentation.utils.selectProfileImage
import com.laixer.swabbr.utils.encodeToBase64
import kotlinx.android.synthetic.main.fragment_registration.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 *  Fragment for handling user registration.
 */
class RegistrationFragment : Fragment() {
    private val authVm: AuthViewModel by sharedViewModel()
    private var selectedProfileImage: Bitmap? = null

    /**
     *  Inflates the view.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    /**
     *  Sets up the UI.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectFeature()

        // Add listeners to each item to enable/disable the register button.
        inputNickname.addTextChangedListener { checkChanges() }
        emailInput.addTextChangedListener { checkChanges() }
        passwordInput.addTextChangedListener { checkChanges() }
        confirmPasswordInput.addTextChangedListener { checkChanges() }

        fab_set_profile_image.setOnClickListener { ImagePicker.selectProfileImage(this) }

        registerButton.setOnClickListener { onClickRegister() }

        authVm.authenticationResultResource.observe(viewLifecycleOwner, Observer { onAuthenticationResult(it) })
    }

    /**
     *  Called when we click the regsitration button.
     */
    private fun onClickRegister() {
        // TODO Split checking functionality, there are more cases than this.
        // We need a minimum password length.
        if (passwordInput.text.toString().length < 8) {
            showMessage("Password must consist of at least 8 characters.")
            return
        }

        // TODO Clean this up.
        authVm.register(
            RegistrationItem(
                email = emailInput.text.toString(),
                password = passwordInput.text.toString(),
                nickname = inputNickname.text.toString(),
                firstName = null,
                lastName = null,
                gender = null,
                country = null,
                birthDate = null,
                // timeZone = //ZonedDateTime.now().offset,
                timeZone = null, // Timezone doesn't work yet
                profileImage = selectedProfileImage?.encodeToBase64(),
                latitude = null,
                longitude = null,
                isPrivate = null,
                dailyVlogRequestLimit = null,
                followMode = null
            )
        )
    }


    // TODO Duplicate functionality with LoginFragment
    /**
     *  Called when a registration (or login) operation completes.
     */
    private fun onAuthenticationResult(res: Resource<Boolean>) {
        when (res.state) {
            ResourceState.LOADING -> {
                loading_icon_registration.visible()
            }
            ResourceState.SUCCESS -> {
                if (res.data == true) {
                    loading_icon_registration.gone()

                    // Go back, as this will always be put on top of the original app stack.
                    // Look at the nav graph for further understanding of how this works,
                    // along with the AuthFragment class.
                    findNavController().popBackStack()
                }
                // No else case is required, as a failure to login/register will
                // result in the resource error state. TODO Kind of suboptimal.
            }
            ResourceState.ERROR -> {
                loading_icon_registration.gone()

                passwordInput.text.clear()
                confirmPasswordInput.text.clear()
                Log.e(TAG, res.message!!)

                showMessage("Could not register")
            }
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
            successCallback = this::onBitmapSelected
        )
    }

    /**
     *  Function that stores and sets our profile image if we select one.
     *
     *  @param selectedBitmap The selected profile image.
     */
    private fun onBitmapSelected(selectedBitmap: Bitmap) {
        this.selectedProfileImage = selectedBitmap
        avatarPicker.setImageBitmap(selectedBitmap)
    }

    /**
     *  Called when we aren't allowed to take pictures for the profile image.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            showMessage("Unable to choose profile image due to invalid permissions.")
        }
    }

    /**
     *  Listens for change to check if the registration button should be enabled or not.
     */
    private fun checkChanges() {
        registerButton.isEnabled = !(
            inputNickname.text.isNullOrEmpty()
                || emailInput.text.isNullOrEmpty()
                || passwordInput.text.isNullOrEmpty()
                || confirmPasswordInput.text.isNullOrEmpty()
            )
            && (passwordInput.text.toString() == confirmPasswordInput.text.toString())
    }

    companion object {
        private const val TAG = "RegistrationFragment"
        private val PUSH_NOTIFICATION_PLATFORM = PushNotificationPlatform.FCM
    }
}
