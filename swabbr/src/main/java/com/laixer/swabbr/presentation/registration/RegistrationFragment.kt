package com.laixer.swabbr.presentation.registration

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.injectFeature
import kotlinx.android.synthetic.main.fragment_login.passwordInput
import kotlinx.android.synthetic.main.fragment_login.registerButton
import kotlinx.android.synthetic.main.fragment_registration.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale
import java.util.TimeZone

class RegistrationFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private val vm: RegistrationViewModel by viewModel()
    private val date = LocalDate.now()
    private var birthDate = date.dayOfMonth
    private var birthMonth = date.monthValue
    private var birthYear = date.year

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareUI()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", resources.configuration.locales[0])

        registerButton.setOnClickListener {
            var birthDateString = birthDate.toString()
            var birthMonthString = birthMonth.toString()
            if (birthDate < ADD_ZERO_BELOW) birthDateString = "0$birthDate"
            if (birthMonth < ADD_ZERO_BELOW) birthMonthString = "0$birthMonth"

            vm.register(
                Registration(
                    firstNameInput.text.toString(),
                    lastNameInput.text.toString(),
                    genderSpinner.selectedItemPosition,
                    countrySpinner.selectedItem.toString(),
                    emailAddressInput.text.toString(),
                    passwordInput.text.toString(),
                    dateFormat.parse(datePicker.text.toString()).toString(),
                    TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT),
                    nicknameInput.text.toString(),
                    "",
                    privateSwitch.isChecked,
                    phoneNumberInput.text.toString()
                )
            )
        }

        injectFeature()

        vm.authorized.observe(viewLifecycleOwner, Observer { register(it) })
    }

    private fun register(res: Resource<Boolean>) {
        when (res.state) {
            ResourceState.LOADING -> {
                progressBar.visible()
            }
            ResourceState.SUCCESS -> {
                progressBar.gone()
                // TODO - Progress login
            }
            ResourceState.ERROR -> {
                passwordInput.text.clear()
                confirmPasswordInput.text.clear()
                progressBar.gone()
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
            requireContext(),
            R.array.gender_array,
            android.R.layout.simple_spinner_item
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
                this.setSelection(countries.indexOf(Locale.getDefault().getDisplayCountry(Locale.getDefault())))
            }
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = it
        }
    }

    private fun prepareDatepicker() {
        datePicker.apply {
            setOnClickListener { showDatePickerDialog() }
            text = getString(
                R.string.birthDatePicker,
                date.dayOfMonth.toString(),
                date.monthValue.toString(),
                date.year.toString()
            )
        }
    }

    private fun prepareSwitch() {
        privateSwitch.setOnCheckedChangeListener { _, _ -> checkChanges() }
    }

    private fun checkChanges() {
        registerButton.isEnabled = !(
            firstNameInput.text.isNullOrEmpty() ||
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
            requireContext(),
            this,
            date.year,
            date.monthValue,
            date.dayOfMonth
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        birthDate = dayOfMonth
        birthMonth = month + 1
        birthYear = year
        val date = "$dayOfMonth/$birthMonth/$year"
        datePicker.text = date
    }

    companion object {
        private const val ADD_ZERO_BELOW = 10
    }
}
