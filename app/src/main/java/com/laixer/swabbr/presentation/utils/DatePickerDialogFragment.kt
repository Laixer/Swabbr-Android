package com.laixer.swabbr.presentation.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDate
import java.util.*

class DatePickerDialogFragment(
    private val currentDate: LocalDate?,
    private val callback: (dateSelected: LocalDate) -> Unit
) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    /**
     *  Creates a date picker popup.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = currentDate?.year ?: c.get(Calendar.YEAR)
        val month = currentDate?.monthValue ?: c.get(Calendar.MONTH)
        val day = currentDate?.dayOfMonth ?: c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    /**
     *  Calls [callback] when we have selected a new date.
     */
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) =
        // Datepicker month starts at 0, LocalDate month starts at 1.
        callback.invoke(LocalDate.of(year, month + 1, dayOfMonth))
}
