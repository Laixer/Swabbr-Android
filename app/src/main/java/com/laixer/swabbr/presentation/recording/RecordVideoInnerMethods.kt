package com.laixer.swabbr.presentation.recording

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.MediaCodec
import android.media.MediaRecorder
import android.os.Handler
import android.util.Log
import android.util.Range
import android.view.Surface
import com.laixer.swabbr.extensions.setOrientationHintFromDirection
import com.laixer.swabbr.presentation.types.CameraInfo
import com.laixer.swabbr.presentation.utils.FixedOrientationFragment
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// TODO Understand suspend.
// TODO Move these methods elsewhere, some utility or camera manager extension class.
/**
 *  Fragment for recording video using the camera2 API.
 */
abstract class RecordVideoInnerMethods : FixedOrientationFragment(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
    // TODO Don't hard code these constants.
    /**
     *  Initializes an existing [MediaRecorder] instance using the provided
     *  [Surface] as input. Note that this expects the [mediaRecorder] to be
     *  in the Initial state. See the state machine for more information at:
     *  https://developer.android.com/reference/android/media/MediaRecorder
     */
    protected fun initializeMediaRecorder(
        mediaRecorder: MediaRecorder,
        cameraInfo: CameraInfo,
        surface: Surface,
        outputFile: File
    ): MediaRecorder =
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setVideoFrameRate(30)
            setVideoEncodingBitRate(5_000_000)
            setAudioEncodingBitRate(192_000)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setInputSurface(surface)
            setOutputFile(outputFile.absolutePath)
            setVideoSize(cameraInfo.size.width, cameraInfo.size.height)
            setOrientationHintFromDirection(cameraInfo.cameraDirection)
        }

    /**
     *  Creates a new recorder [Surface]. Don't forget to release this
     *  surface when you are done with it.
     */
    protected fun createRecorderSurface(cameraInfo: CameraInfo, outputFile: File): Surface {
        // Get a persistent Surface from MediaCodec, don't forget to release when done.
        val surface = MediaCodec.createPersistentInputSurface()

        // Prepare and release a dummy MediaRecorder with our new surface.
        // Required to allocate an appropriately sized buffer before passing
        // the Surface as the output target to the capture session.
        initializeMediaRecorder(MediaRecorder(), cameraInfo, surface, outputFile).apply {
            prepare()
            release()
        }

        return surface
    }

    /**
     *  Creates a capture request to bind some recording session to multiple surfaces.
     *  This can also explicitly set the fps for each surface, but this is optional.
     */
    protected fun createCaptureRequest(session: CameraCaptureSession, surfaces: List<Surface>, fps: Int? = null) =
        // Capture request holds references to target surfaces
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
            // Add each surface to the output target list.
            surfaces.forEach { addTarget(it) }

            // Sets user requested FPS for all targets if specified.
            fps?.let { fps ->
                set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, Range(fps, fps))
            }
        }.build()

    /**
     *  Creates a [CameraCaptureSession] and returns the configured session.
     *  Note that this method is suspended.
     */
    protected suspend fun createCameraCaptureSession(
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->
        // Creates a capture session using the predefined targets, and defines a session state
        // callback which resumes the coroutine once the session is configured
        device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {

            // Continue when we succeed.
            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

            // Throw when we fail.
            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e(TAG, exc.message, exc)
                cont.resumeWithException(exc)
            }
        }, handler)
    }

    /**
     *  Opens a camera object and returns the opened device. Note that this
     *  method is suspend and thus will returns as a coroutine result.
     */
    @SuppressLint("MissingPermission")
    protected suspend fun openCamera(
        cameraManager: CameraManager,
        cameraId: String,
        handler: Handler? = null
    ): CameraDevice = suspendCancellableCoroutine { cancellableContinuation ->
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) = cancellableContinuation.resume(device)

            override fun onDisconnected(device: CameraDevice) {
                Log.w(TAG, "Camera $cameraId has been disconnected")
            }

            override fun onError(device: CameraDevice, error: Int) {
                val msg = when (error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }
                val exc = RuntimeException("Camera $cameraId error: ($error) $msg")
                Log.e(TAG, exc.message, exc)

                if (cancellableContinuation.isActive) {
                    cancellableContinuation.resumeWithException(exc)
                }
            }
        }, handler)
    }

    companion object {
        private const val TAG = "RecordVideoInnerMethods"
    }
}
