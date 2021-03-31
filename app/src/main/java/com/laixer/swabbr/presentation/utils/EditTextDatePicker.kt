package com.laixer.swabbr.presentation.utils

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.fragment.app.FragmentManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditTextDatePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatEditText(context, attrs, defStyleAttr) {
    /**
     *  Setup component.
     */
    init {
        inputType = InputType.TYPE_NULL
    }

    /**
     *  Stored callback.
     */
    private lateinit var callback: (dateSelected: LocalDate) -> Unit

    /**
     *  Currently selected date.
     */
    private var currentDate: LocalDate? = null

    /**
     *  Sets up the callback for when we select this item.
     */
    fun setupClickListener(manager: FragmentManager, title: String, callback: (dateSelected: LocalDate) -> Unit) {
        setOnClickListener {
            this.callback = callback
            DatePickerDialogFragment(currentDate, ::onDateSelected).show(manager, title)
        }
    }

    /**
     *  Update the text view display of said date.
     */
    fun setDate(date: LocalDate?) {
        currentDate = date
        this.setText(currentDate?.format(DateTimeFormatter.ISO_DATE) ?: "")
    }

    /**
     * Clear the date for a date edit text.
     */
    fun clearDate() {
        this.text = null
    }

    /**
     *  Called when a date is selected.
     */
    private fun onDateSelected(dateSelected: LocalDate) {
        setDate(dateSelected)
        this.callback.invoke(dateSelected) // TODO Dangerous, can't guarantee init
    }
}
