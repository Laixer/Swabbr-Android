package com.laixer.swabbr.presentation.recording

import android.Manifest
import android.content.Intent
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.*
import androidx.lifecycle.lifecycleScope
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.goBack
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.MainActivity
import com.laixer.swabbr.utils.FileHelper.Companion.createFile
import com.laixer.swabbr.utils.getPreviewOutputSize
import kotlinx.android.synthetic.main.dev_fragment_record_video.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 *  Fragment for recording video using the camera2 API.
 */
class DevRecordVideoFragment : RecordVideoInnerMethods() {
    /**
     *  Used to enumerate and open camera instances.
     */
    private val cameraManager: CameraManager by lazy { getCameraManagerFromContext() }

    /**
     *  Camera object which will be opened by this fragment.
     */
    private lateinit var camera: CameraDevice

    // TODO Should this start right away?
    /**
     *  [HandlerThread] where all camera operations run.
     */
    private val cameraThread = HandlerThread("CameraThread").apply { start() }

    /**
     *  [Handler] corresponding to [cameraThread].
     */
    private val cameraHandler = Handler(cameraThread.looper)

    /**
     *  Object representing a video recording session.
     */
    private lateinit var cameraCaptureSession: CameraCaptureSession

    /**
     * Setup a persistent [Surface] for the recorder so we can use it as an output target for the
     * camera session without preparing the recorder
     */
    private val recorderSurface: Surface by lazy { createRecorderSurface(outputFile) }

    /**
     *  Media recorder which saves our recording.
     */
    private val recorder: MediaRecorder by lazy { createRecorder(recorderSurface, outputFile) }

    /**
     *  File where our recording will be stored.
     */
    private val outputFile: File by lazy { createFile(requireContext(), "mp4") }

    /**
     *  Requests used only for preview in the [CameraCaptureSession].
     */
    private val previewRequest: CaptureRequest by lazy {
        createCaptureRequest(cameraCaptureSession, listOf(dev_surface_view.holder.surface))
    }

    /**
     *  Requests used for preview and recording in the [CameraCaptureSession].
     *  Note that this does more than [previewRequest].
     */
    private val recordRequest: CaptureRequest by lazy {
        // TODO Hard coded FPS.
        createCaptureRequest(cameraCaptureSession, listOf(dev_surface_view.holder.surface, recorderSurface), 30)
    }

    /**
     *  Ask for permissions right away.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askPermission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO) {
            // Do nothing
        }.onDeclined {
            // At least one permission has been declined by the user
            showMessage("Your permissions are required to record videos")

            // Go back.
            goBack()
        }
    }

    /**
     *  Inflate a generic recording fragment.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dev_fragment_record_video, container, false)

    /**
     *  Starts our camera initialization process.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assign setup callbacks to the holder of our surface view so that the
        // camera will be setup once said element has been inflated completely.
        dev_surface_view.holder.addCallback(object : SurfaceHolder.Callback {
            /**
             *  Called immediately after the surface is first created.
             */
            override fun surfaceCreated(holder: SurfaceHolder?) {
                // Get an object representing the size of our auto fit surface view,
                // then assign said sizes to the auto fit surface view.
                val previewSize: Size = getPreviewOutputSize(
                    display = dev_surface_view.display,
                    characteristics = getFirstCameraCharacteristics(cameraManager),
                    targetClass = SurfaceHolder::class.java
                )
                dev_surface_view.setAspectRatio(previewSize.width, previewSize.height)

                // To ensure that size is set we initialize the camera in the view's thread.
                dev_surface_view.post { initializeCamera(cameraManager.cameraIdList.first()) }      // TODO Dangerous
            }

            /**
             * This is called immediately after any structural changes (format or
             * size) have been made to the surface. This is not relevant for us.
             */
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) = Unit

            /**
             *  This is called immediately before a surface is being destroyed.
             *  This is not relevant for us.
             */
            override fun surfaceDestroyed(holder: SurfaceHolder?) = Unit
        })
    }

    /**
     *  Opens and sets up our camera object on the main thread.
     */
    private fun initializeCamera(cameraId: String) = lifecycleScope.launch(Dispatchers.Main) {
        // Open the camera.
        camera = openCamera(cameraManager, cameraId)

        // Target the output to our surface view and to our recorder surface.
        val targets = listOf(dev_surface_view.holder.surface, recorderSurface)

        // Create a new capture session.
        cameraCaptureSession = createCameraCaptureSession(camera, targets, cameraHandler)

        /**
         *  Sends the capture request as frequently as possible until the session
         *  is torn down or [CameraCaptureSession.stopRepeating] is called.
         */
        cameraCaptureSession.setRepeatingRequest(previewRequest, null, cameraHandler)
    }

    private fun tryStartRecording() {

    }

    private fun tryStopRecording() {

    }
}
