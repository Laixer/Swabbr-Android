package com.laixer.swabbr.extensions

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.MediaRecorder
import android.util.Size
import com.laixer.swabbr.domain.exceptions.CameraException
import com.laixer.swabbr.presentation.types.CameraDirection
import com.laixer.swabbr.presentation.types.CameraInfo

/**
 *  Gets the first front facing camera we can find or the first
 *  camera we can find if none are present.
 */
fun CameraManager.getFirstFrontFacingCameraId(): String {
    if (cameraIdList.isEmpty()) {
        throw CameraException("No cameras were available")
    }

    val frontFacingIds = cameraIdList.filter { id ->
        getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
    }

    return frontFacingIds.firstOrNull() ?: cameraIdList.first()
}

/**
 *  Gets the first back facing camera we can find or the first
 *  camera we can find if none are present.
 */
fun CameraManager.getFirstBackFacingCameraId(): String {
    if (cameraIdList.isEmpty()) {
        throw CameraException("No cameras were available")
    }

    val backFacingIds = cameraIdList.filter { id ->
        getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
    }

    return backFacingIds.firstOrNull() ?: cameraIdList.first()
}

/**
 *  Gets [CameraCharacteristics] for the result of [getFirstFrontFacingCameraId].
 */
fun CameraManager.getFirstFrontFacingCameraCharacteristics(): CameraCharacteristics =
    getCameraCharacteristics(getFirstFrontFacingCameraId())

/**
 *  Gets [CameraCharacteristics] for the result of [getFirstBackFacingCameraId].
 */
fun CameraManager.getFirstBackFacingCameraCharacteristics(): CameraCharacteristics =
    getCameraCharacteristics(getFirstBackFacingCameraId())

/**
 *  Gets the result of [CameraCharacteristics.LENS_FACING] for a camera id.
 */
fun CameraManager.getCameraFacingInt(cameraId: String): Int? =
    getCameraCharacteristics(cameraId).get(CameraCharacteristics.LENS_FACING)

/**
 *  Translates [CameraCharacteristics.LENS_FACING] to a [CameraDirection] enum
 *  for a specified camera id.
 */
fun CameraManager.getCameraDirection(cameraId: String): CameraDirection =
    getCameraDirection(getCameraCharacteristics(cameraId))

/**
 *  Translates [CameraCharacteristics.LENS_FACING] to a [CameraDirection] enum.
 */
fun CameraManager.getCameraDirection(cameraCharacteristics: CameraCharacteristics): CameraDirection =
    when (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)) {
        CameraCharacteristics.LENS_FACING_BACK -> CameraDirection.BACK
        CameraCharacteristics.LENS_FACING_FRONT -> CameraDirection.FRONT
        else -> CameraDirection.OTHER
    }

/**
 *  Gets a [CameraInfo] object based on a camera id. This will try to match the
 *  [preferredSize] and does a best effort (see comments of this method) when no
 *  exact match is found.
 */
fun CameraManager.getCameraInfo(cameraId: String, preferredSize: Size): CameraInfo {
    val cameraCharacteristics = getCameraCharacteristics(cameraId)
    val streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
    val outputSizes = streamConfigurationMap.getOutputSizes(MediaRecorder::class.java)

    if (outputSizes.isEmpty()) {
        throw CameraException("Could not find any valid camera output sizes")
    }

    // TODO How we exactly want to choose this may be an improvement for the future.
    //      In good weather we just get our desired size which will probably be 1080p.
    //      In bad weather we might get huge dimensions or weird aspect ratios.
    // Select the preferred size, else the one closest to its surface area, else the one with the biggest area.
    val selectedSize = outputSizes
        .firstOrNull { size -> size.width == preferredSize.width && size.height == preferredSize.height }
        ?: outputSizes
            .sortedWith(compareBy { it.height * it.width })
            .firstOrNull { it.height * it.width <= preferredSize.height * preferredSize.width }
        ?: outputSizes.sortedWith(compareBy { it.height * it.width }).first()

    return CameraInfo(
        cameraId = cameraId,
        cameraDirection = getCameraDirection(cameraCharacteristics),
        fps = 30, // TODO Hard coded for now
        size = selectedSize
    )
}


