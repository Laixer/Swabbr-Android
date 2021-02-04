package com.laixer.swabbr.presentation.auth.registration

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.iid.FirebaseInstanceId
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.types.PushNotificationPlatform
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.model.RegistrationItem
import com.laixer.swabbr.presentation.model.UserCompleteItem
import com.laixer.swabbr.presentation.utils.onActivityResult
import com.laixer.swabbr.presentation.utils.selectProfileImage
import com.laixer.swabbr.utils.encodeToBase64
import kotlinx.android.synthetic.main.fragment_registration.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class RegistrationFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private val vm: AuthViewModel by sharedViewModel()
    private var selectedProfileImage: Bitmap? = null
    private val firebaseInstanceId by lazy { FirebaseInstanceId.getInstance().id }

    //    private val date = LocalDate.now()
//    private var selectedDate: ZonedDateTime = ZonedDateTime.now()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectFeature()

        // TODO Look into this
        savedInstanceState?.getParcelable<Bitmap?>("BitmapImage")?.let {
            selectedProfileImage = it
            avatarPicker::setImageBitmap
        }

        prepareUI()

        /**
         *  This listener registers the user. Note that a lot of the
         *  registration values are nullable and are missing at this
         *  moment. These registration options can be added later.
         */
        registerButton.setOnClickListener {
            if (passwordInput.text.toString().length < 8) {
                Toast.makeText(requireContext(), "Password must consist of at least 8 characters.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // FUTURE More properties can be added later.
            vm.register(
                RegistrationItem(
                    email = emailInput.text.toString(),
                    password = passwordInput.text.toString(),
                    nickname = inputNickname.text.toString(),
                    firstName = null,
                    lastName = null,
                    gender = null,
                    country = null,
                    birthDate = null,
                    // timeZone = //ZonedDateTime.now().offset, // TODO Is this correct?
                    // TODO This doesn't work
                    timeZone = null, // TODO Is this correct?
                    profileImage = selectedProfileImage?.encodeToBase64(),
                    latitude = null,
                    longitude = null,
                    isPrivate = null,
                    dailyVlogRequestLimit = null,
                    followMode = null
                ),
                firebaseToken = firebaseInstanceId
            )
        }

        vm.authenticatedUser.observe(viewLifecycleOwner, Observer(this@RegistrationFragment::register))
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putParcelable("BitmapImage", selectedBitmap)
//    }

    private fun register(res: Resource<UserCompleteItem?>) {
        when (res.state) {
            ResourceState.LOADING -> {
                progressBar.visible()
            }
            ResourceState.SUCCESS -> {
                res.data?.let {
                    progressBar.gone()
                    // Nav to main app is handled by our parent activity (AuthActivity)
                } ?: run {
                    Toast.makeText(
                        requireActivity().applicationContext,
                        "Registration successful, signing in you in…",
                        Toast.LENGTH_SHORT
                    ).show()
                    vm.login(
                        emailInput.text.toString(),
                        passwordInput.text.toString(),
                        firebaseInstanceId
                    )
                }
            }
            ResourceState.ERROR -> {
                passwordInput.text.clear()
                confirmPasswordInput.text.clear()
                progressBar.gone()
                Log.e(TAG, res.message!!)
                Toast.makeText(requireActivity().applicationContext, res.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun prepareUI() {
        prepareTextInputs()

//        prepareSpinners()
//        prepareDatepicker()
//        prepareSwitch()

        fab_set_profile_image.setOnClickListener { ImagePicker.selectProfileImage(this) }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(
                requireActivity(),
                "Unable to choose profile image due to invalid permissions.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun prepareTextInputs() {
        val textInputWatcher: TextWatcher = object : TextWatcher {
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
//        firstNameInput.addTextChangedListener(textInputWatcher)
//        lastNameInput.addTextChangedListener(textInputWatcher)
//        phoneNumberInput.addTextChangedListener(textInputWatcher)
        inputNickname.addTextChangedListener(textInputWatcher)
        emailInput.addTextChangedListener(textInputWatcher)
        passwordInput.addTextChangedListener(textInputWatcher)
        confirmPasswordInput.addTextChangedListener(textInputWatcher)
    }

    //    private fun prepareSpinners() {
//        val telephonyManager = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        val isoCountry = telephonyManager.networkCountryIso
//        val spinnerWatcher = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                return
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                checkChanges()
//            }
//        }
//        with(spinnerWatcher) {
//            genderSpinner.onItemSelectedListener = this
//            countrySpinner.onItemSelectedListener = this
//        }
//        ArrayAdapter.createFromResource(
//            requireContext(), R.array.gender_array, android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            countrySpinner.adapter = adapter
//        }
//        val countries = Locale.getAvailableLocales().map {
//            it.getDisplayCountry(Locale.getDefault())
//        }.filter { !it.isNullOrEmpty() }.toSortedSet().toTypedArray()
//
//        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countries).apply {
//            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        }.also { adapter ->
//            countrySpinner.apply {
//                this.adapter = adapter
//                this.setSelection(countries.indexOfFirst { country ->
//                    Locale.getAvailableLocales().first {
//                        // Automatically select country based on users network
//                        it.country == isoCountry.toUpperCase(Locale.ROOT)
//                    }.displayCountry == country
//                })
//            }
//        }
//
//        ArrayAdapter.createFromResource(
//            requireContext(), R.array.gender_array, android.R.layout.simple_spinner_item
//        ).also {
//            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            genderSpinner.adapter = it
//        }
//    }
//    private fun prepareDatepicker() {
//        datePicker.apply {
//            setOnClickListener { showDatePickerDialog() }
//            text = getString(
//                R.string.birthDatePicker, date.dayOfMonth.toString(), date.monthValue.toString(), date.year.toString()
//            )
//        }
//    }
//    private fun prepareSwitch() {
//        privateSwitch.setOnCheckedChangeListener { _, _ -> checkChanges() }
//    }
    private fun checkChanges() {
        registerButton.isEnabled =
            !(
//                firstNameInput.text.isNullOrEmpty() ||
//                lastNameInput.text.isNullOrEmpty() ||
//                phoneNumberInput.text.isNullOrEmpty() ||
                inputNickname.text.isNullOrEmpty() ||
                    emailInput.text.isNullOrEmpty() ||
                    passwordInput.text.isNullOrEmpty() ||
                    confirmPasswordInput.text.isNullOrEmpty()) &&
                (passwordInput.text.toString() == confirmPasswordInput.text.toString()
                    )
    }

    //    private fun showDatePickerDialog() {
//        DatePickerDialog(
//            requireContext(), this, date.year, date.monthValue, date.dayOfMonth
//        ).apply {
//            datePicker.maxDate = System.currentTimeMillis()
//            show()
//        }
//    }
    override fun onDateSet(view: DatePicker, year: Int, month: Int /* 0-11 */, dayOfMonth: Int) {
//        selectedDate = selectedDate.withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth)
//        datePicker.text = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    companion object {
        private const val TAG = "RegistrationFragment"
        private val PUSH_NOTIFICATION_PLATFORM = PushNotificationPlatform.FCM
    }
}
