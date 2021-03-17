package com.laixer.swabbr.presentation.utils

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

// TODO Do we really want this to behave like this? Maybe not, maybe do this in the activity? ...
/**
 *  Fragment base which fixes the orientation of the phone. When this
 *  exited in any way the orientation will no longer be fixed.
 *
 *  @param orientation One of [ActivityInfo.SCREEN_ORIENTATION_PORTRAIT]
 *   or [ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE].
 */
abstract class FixedOrientationFragment(private val orientation: Int) : Fragment() {
    /**
     *  This stores the original orientation before modifying it here.
     */
    private var originalFlags: Int? = null

    /**
     *  Save the current orientation flags and assign portrait mode.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isDesiredOrientationValid()) {
            Log.w(TAG, "Invalid orientation flag specified, this will do nothing. Flag: $orientation")
            return
        }

        originalFlags = requireActivity().requestedOrientation
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     *  Undo our changes.
     */
    override fun onPause() {
        super.onPause()

        // Check again to prevent weird bugs from appearing in the future.
        if (isDesiredOrientationValid()) {
            originalFlags?.let {
                requireActivity().requestedOrientation = it
            }
        }
    }

    /**
     *  Checks if [orientation] is valid.
     */
    private fun isDesiredOrientationValid() = orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ||
        orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    companion object {
        private val TAG = this::class.java.simpleName
    }
}
