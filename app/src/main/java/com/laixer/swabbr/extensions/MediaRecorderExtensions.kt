package com.laixer.swabbr.extensions

import android.media.MediaRecorder
import com.laixer.swabbr.presentation.types.CameraDirection

/**
 *  Sets a value for [MediaRecorder.setOrientationHint] based on a camera direction.
 */
fun MediaRecorder.setOrientationHintFromDirection(direction: CameraDirection) {
    setOrientationHint(
        when (direction) {
            CameraDirection.FRONT -> 270
            CameraDirection.BACK -> 90
            else -> 90 // This will probably be incorrect but it's the best we can do.
        }
    )
}
