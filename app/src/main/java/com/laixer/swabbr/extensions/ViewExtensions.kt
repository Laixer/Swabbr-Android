package com.laixer.swabbr.extensions

import android.content.res.ColorStateList
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 *  Wrapper around the correct way to set the icon/background color for a view.
 */
fun View.setBackgroundTint(@ColorRes resourceId: Int) {
    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, resourceId))
}

/**
 *  Wrapper around the correct way to set the icon/background color for a progress bar.
 */
fun ProgressBar.setProgressTint(@ColorRes resourceId: Int) {
    progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, resourceId))
}



