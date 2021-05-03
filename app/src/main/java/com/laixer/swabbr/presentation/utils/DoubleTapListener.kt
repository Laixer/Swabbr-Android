package com.laixer.swabbr.presentation.utils

import android.app.Activity
import android.view.GestureDetector
import android.view.MotionEvent

/**
 *  Builds a double tap listener that calls [callback] on double tap.
 *
 *  @param activity The activity in which this exists.
 *  @param callback The action to perform on double tap.
 */
private fun buildDoubleTapListener(activity: Activity, callback: (e: MotionEvent?) -> Unit): GestureDetector =
    GestureDetector(
        activity,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                // Perform the callback
                callback.invoke(e)

                // Return true to indicate the double tap was handled.
                return true
            }
        })

/**
 *  Builds a double tap listener that calls [callback] on double tap.
 *
 *  @param activity The activity in which this exists.
 *  @param callback The action to perform on double tap.
 */
fun buildDoubleTapListener(activity: Activity, callback: () -> Unit): GestureDetector {
    // Declare a callback that simply doesn't use the motion event first.
    val cb: (e: MotionEvent?) -> Unit = { callback.invoke() }

    return buildDoubleTapListener(activity, cb)
}
