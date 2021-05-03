package com.laixer.swabbr.utils

import android.content.Context
import com.laixer.swabbr.R

/**
 *  Formats a number into a number string using the string resources.
 */
fun Context.formatNumber(number: Int): String = this.getString(R.string.count, number)
