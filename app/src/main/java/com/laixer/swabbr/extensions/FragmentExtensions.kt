package com.laixer.swabbr.extensions

import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 *  Builds a generic toast message and displays it.
 *
 *  @param message The message to display.
 */
fun Fragment.showMessage(message: String) = Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
