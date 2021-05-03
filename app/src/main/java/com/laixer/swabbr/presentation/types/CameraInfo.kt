package com.laixer.swabbr.presentation.types

import android.util.Size

/**
 *  Class representing basic camera parameters.
 */
data class CameraInfo(
    val cameraId: String,
    val size: Size,
    val fps: Int,
    val cameraDirection: CameraDirection
)
