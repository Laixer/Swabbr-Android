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
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.hideSoftKeyboard
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.MainActivity
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.model.RegistrationItem
import com.laixer.swabbr.presentation.utils.onActivityResult
import com.laixer.swabbr.presentation.utils.selectProfileImage
import com.laixer.swabbr.presentation.utils.todosortme.gone
import com.laixer.swabbr.presentation.utils.todosortme.visible
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import kotlinx.android.synthetic.main.fragment_registration.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File

/**
 *  Fragment for handling user registration.
 */
class RegistrationFragment : Fragment() {

    private val authVm: AuthViewModel by sharedViewModel()

    /**
     *  Assigned by the image picker, optional.
     */
    private var selectedProfileImageFile: File? = null

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

        // Hide the bottom swabbr navigation bar.
        (requireActivity() as MainActivity).tryHideBottomBar()

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

        if (passwordInput.text.toString().length < 8) {
            showMessage("Password must consist of at least 8 characters.")
            return
        }

        if (passwordInput.text.toString() != confirmPasswordInput.text.toString()) {
            showMessage("Passwords must match")
            return
        }

        // Explicitly hide the input keyboard as Android doesn't do this for us.
        hideSoftKeyboard()

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
                timeZone = null, // TODO Timezone doesn't work yet
                latitude = null,
                longitude = null,
                isPrivate = null,
                dailyVlogRequestLimit = null,
                followMode = null
            ), selectedProfileImageFile
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

                    // Show the bottom swabbr navigation bar.
                    (requireActivity() as MainActivity).tryShowBottomBar()

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

                // TODO Do we want this? Annoying app flow though if we enable it...
                // passwordInput.text.clear()
                // confirmPasswordInput.text.clear()

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
     */
    private fun onBitmapSelected(imageFile: File, imageBitmap: Bitmap) {
        selectedProfileImageFile = imageFile
        avatarPicker.setImageBitmap(imageBitmap)
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
    }
}
