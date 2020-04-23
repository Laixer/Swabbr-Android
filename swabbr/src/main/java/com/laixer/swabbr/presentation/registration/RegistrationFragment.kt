package com.laixer.swabbr.presentation.registration

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.firebase.iid.FirebaseInstanceId
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.model.Gender
import com.laixer.swabbr.domain.model.PushNotificationPlatform
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.laixer.swabbr.presentation.model.AuthUserItem
import com.laixer.swabbr.presentation.model.RegistrationItem
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.fragment_registration.passwordInput
import kotlinx.android.synthetic.main.fragment_registration.progressBar
import kotlinx.android.synthetic.main.fragment_registration.registerButton
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class RegistrationFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private val vm: AuthViewModel by viewModel()
    private val date = LocalDate.now()
    private var selectedDate: ZonedDateTime = ZonedDateTime.now()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareUI()
        registerButton.setOnClickListener {
            vm.register(
                RegistrationItem(
                    firstNameInput.text.toString(),
                    lastNameInput.text.toString(),
                    Gender.values().firstOrNull { it.ordinal == genderSpinner.selectedItemPosition }
                        ?: Gender.UNSPECIFIED,
                    Locale.getAvailableLocales()
                        .first { it.displayCountry == countrySpinner.selectedItem.toString() }.country,
                    emailAddressInput.text.toString(),
                    passwordInput.text.toString(),
                    selectedDate,
                    ZoneId.systemDefault().rules.getOffset(Instant.now()),
                    nicknameInput.text.toString(),
                    URL("https://api.adorable.io/avatars/285/${nicknameInput.text}"),
                    phoneNumberInput.text.toString(),
                    PushNotificationPlatform.FCM,
                    FirebaseInstanceId.getInstance().id
                ),
                rememberMeSwitch.isChecked
            )
        }

        injectFeature()

        vm.authenticatedUser.observe(viewLifecycleOwner, Observer { register(it) })
    }

    private fun register(res: Resource<AuthUserItem>) {
        when (res.state) {
            ResourceState.LOADING -> {
                progressBar.visible()
            }
            ResourceState.SUCCESS -> {
                progressBar.gone()
                requireActivity().onBackPressed()
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
        prepareSpinners()
        prepareDatepicker()
        prepareSwitch()
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
        firstNameInput.addTextChangedListener(textInputWatcher)
        lastNameInput.addTextChangedListener(textInputWatcher)
        phoneNumberInput.addTextChangedListener(textInputWatcher)
        nicknameInput.addTextChangedListener(textInputWatcher)
        emailAddressInput.addTextChangedListener(textInputWatcher)
        passwordInput.addTextChangedListener(textInputWatcher)
        confirmPasswordInput.addTextChangedListener(textInputWatcher)
    }

    private fun prepareSpinners() {
        val telephonyManager = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val isoCountry = telephonyManager.networkCountryIso
        val spinnerWatcher = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                checkChanges()
            }
        }

        with(spinnerWatcher) {
            genderSpinner.onItemSelectedListener = this
            countrySpinner.onItemSelectedListener = this
        }

        ArrayAdapter.createFromResource(
            requireContext(), R.array.gender_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            countrySpinner.adapter = adapter
        }
        val countries = Locale.getAvailableLocales().map {
            it.getDisplayCountry(Locale.getDefault())
        }.filter { !it.isNullOrEmpty() }.toSortedSet().toTypedArray()

        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countries).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }.also { adapter ->
            countrySpinner.apply {
                this.adapter = adapter
                this.setSelection(countries.indexOfFirst { country ->
                    Locale.getAvailableLocales().first {
                        // Automatically select country based on users network
                        it.country == isoCountry.toUpperCase(Locale.ROOT)
                    }.displayCountry == country
                })
            }
        }

        ArrayAdapter.createFromResource(
            requireContext(), R.array.gender_array, android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = it
        }
    }

    private fun prepareDatepicker() {
        datePicker.apply {
            setOnClickListener { showDatePickerDialog() }
            text = getString(
                R.string.birthDatePicker, date.dayOfMonth.toString(), date.monthValue.toString(), date.year.toString()
            )
        }
    }

    private fun prepareSwitch() {
        privateSwitch.setOnCheckedChangeListener { _, _ -> checkChanges() }
    }

    private fun checkChanges() {
        registerButton.isEnabled =
            !(firstNameInput.text.isNullOrEmpty() ||
                lastNameInput.text.isNullOrEmpty() ||
                phoneNumberInput.text.isNullOrEmpty() ||
                nicknameInput.text.isNullOrEmpty() ||
                emailAddressInput.text.isNullOrEmpty() ||
                passwordInput.text.isNullOrEmpty() ||
                confirmPasswordInput.text.isNullOrEmpty()) &&
                (passwordInput.text.toString() == confirmPasswordInput.text.toString())
    }

    private fun showDatePickerDialog() {
        DatePickerDialog(
            requireContext(), this, date.year, date.monthValue, date.dayOfMonth
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int /* 0-11 */, dayOfMonth: Int) {
        selectedDate = selectedDate.withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth)
        datePicker.text = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    companion object {
        private const val TAG = "RegistrationFragment"
        private const val ADD_ZERO_BELOW = 10
    }
}
