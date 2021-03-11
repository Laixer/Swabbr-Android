package com.laixer.swabbr.presentation.recording

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Camera
import android.hardware.camera2.*
import android.media.MediaCodec
import android.media.MediaRecorder
import android.os.Handler
import android.util.Log
import android.util.Range
import android.view.Surface
import androidx.fragment.app.Fragment
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// TODO Understand suspend.
/**
 *  Fragment for recording video using the camera2 API.
 */
abstract class RecordVideoInnerMethods : Fragment() {
    /**
     *  Gets the [CameraManager] from our [Context].
     */
    protected fun getCameraManagerFromContext(): CameraManager =
        requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager

    /**
     *  Creates a [MediaRecorder] instance using the provided [Surface] as input
     */
    protected fun createRecorder(surface: Surface, outputFile: File) = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setVideoSource(MediaRecorder.VideoSource.SURFACE)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setVideoEncodingBitRate(3_000_000)                  // TODO Don't hard code
        setAudioEncodingBitRate(192_000)                    // TODO Don't hard code
        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setInputSurface(surface)

        // TODO
        setOutputFile(outputFile.absolutePath)
        // setVideoFrameRate(args.fps)
        // setVideoSize(args.width, args.height)
    }

    /**
     *  Creates a new recorder [Surface].
     */
    protected fun createRecorderSurface(outputFile: File): Surface {
        // Get a persistent Surface from MediaCodec, don't forget to release when done.
        val surface = MediaCodec.createPersistentInputSurface()

        // Prepare and release a dummy MediaRecorder with our new surface.
        // Required to allocate an appropriately sized buffer before passing
        // the Surface as the output target to the capture session.
        createRecorder(surface, outputFile).apply {
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

    // TODO Debug
    /**
     *  Gets the first available camera characteristics.
     */
    protected fun getFirstCameraCharacteristics(cameraManager: CameraManager): CameraCharacteristics =
        cameraManager.getCameraCharacteristics(cameraManager.cameraIdList.first())

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
                requireActivity().finish()
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
