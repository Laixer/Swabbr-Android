package com.laixer.swabbr.extensions

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.MediaRecorder

/**
 *  Gets a value for [MediaRecorder.setOrientationHint] based on a camera id. This requires the
 *  camera manager to be able to extract the direction which the camera is facing.
 */
fun MediaRecorder.getOrientationHintFromCameraId(cameraManager: CameraManager, cameraId: String) =
    when (cameraManager.getCameraFacingInt(cameraId)) {
        CameraCharacteristics.LENS_FACING_FRONT -> 270
        CameraCharacteristics.LENS_FACING_BACK -> 90
        else -> 90 // This will probably be incorrect but it's the best we can do.
    }
