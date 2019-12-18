package com.laixer.swabbr.presentation.registration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import com.laixer.navigation.features.SampleNavigation
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.settings.RegistrationViewModel
import kotlinx.android.synthetic.main.activity_login.passwordInput
import kotlinx.android.synthetic.main.activity_login.registerButton
import kotlinx.android.synthetic.main.activity_registration.*
import org.koin.androidx.viewmodel.ext.viewModel
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog
import android.widget.DatePicker

class RegistrationActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private val vm: RegistrationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        prepareUI()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        registerButton.setOnClickListener {
            vm.register(
                Registration(
                    firstNameInput.text.toString(),
                    lastNameInput.text.toString(),
                    genderSpinner.selectedItemPosition,
                    countrySpinner.selectedItem.toString(),
                    emailAddressInput.text.toString(),
                    passwordInput.text.toString(),
                    dateFormat.parse(birthDatePicker.text.toString()),
                    TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT),
                    nicknameInput.text.toString(),
                    "",
                    privateSwitch.isChecked,
                    phoneNumberInput.text.toString()
                )
            )
        }

        injectFeature()

        vm.authorized.observe(this, Observer { register(it) })
    }

    private fun register(res: Resource<Boolean>) {
        when (res.state) {
            ResourceState.LOADING -> {
            }
            ResourceState.SUCCESS -> {
                startActivity(SampleNavigation.vlogList())
            }
            ResourceState.ERROR -> {
                passwordInput.text.clear()
                Toast.makeText(this, res.message, Toast.LENGTH_SHORT).show()
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

        genderSpinner.onItemSelectedListener = spinnerWatcher
        countrySpinner.onItemSelectedListener = spinnerWatcher

        ArrayAdapter.createFromResource(
            this,
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            countrySpinner.adapter = adapter
        }

        val countries = Locale.getAvailableLocales().map {
            it.getDisplayCountry(Locale.getDefault())
        }.filter { !it.isNullOrEmpty() }.toSortedSet().toTypedArray()

        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = adapter
        countrySpinner.setSelection(countries.indexOf(Locale.getDefault().getDisplayCountry(Locale.getDefault())))

        ArrayAdapter.createFromResource(
            this,
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = it
        }
    }

    private fun prepareDatepicker() {
        birthDatePicker.text = getString(
            R.string.birthDatePicker,
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString(),
            Calendar.getInstance().get(Calendar.MONTH).toString(),
            Calendar.getInstance().get(Calendar.YEAR).toString()
        )
        birthDatePicker.setOnClickListener { showDatePickerDialog() }
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
        val datePickerDialog = DatePickerDialog(
            this,
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val date = "$dayOfMonth/$month/$year"
        birthDatePicker.text = date
    }
}
