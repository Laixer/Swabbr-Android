package com.laixer.swabbr.presentation.reaction

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.camera2.*
import android.media.MediaCodec
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Range
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.laixer.swabbr.BuildConfig
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.vlogs.details.ReactionViewModel
import com.laixer.swabbr.utils.AutoFitSurfaceView
import com.laixer.swabbr.utils.Utils
import com.laixer.swabbr.utils.getPreviewOutputSize
import io.antmedia.android.broadcaster.utils.OrientationLiveData
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.android.synthetic.main.video_confirm_dialogue.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 *  Fragment for recording a reaction. The reaction is buffered into a file,
 *  and a playback & confirmation popup is shown after recording. When the
 *  user decides to proceed with the reaction, the file is uploaded to the
 *  blob storage and the backend is notified of this.
 *
 *  This activity uses the camera/camcorder as the A/V source for the [android.media.MediaRecorder] API.
 *  A [android.view.TextureView] is used as the camera preview which limits the code to API 14+. This
 *  can be easily replaced with a [android.view.SurfaceView] to run on older devices.
 */
class RecordReactionFragment : Fragment() {
    /** AndroidX navigation arguments */
    private val args: RecordReactionFragmentArgs by navArgs()
    private val reactionVm: ReactionViewModel by viewModel()

    private val targetVlogId: UUID by lazy { UUID.fromString(args.vlogId) }

    private var recording = false

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        val context = requireContext().applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** [CameraCharacteristics] corresponding to the provided Camera ID */
    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics(args.cameraId)
    }

    /**
     * File where the recording will be saved
     */
    private val outputFile: File by lazy { createFile(requireContext(), "mp4") }

    private val supportedCameraSizeList: List<CameraInfo> by lazy {
        enumerateVideoCameras(cameraManager)
    }

    private val recommendedCameraInfo: CameraInfo? by lazy {
        supportedCameraSizeList.lastOrNull { it.size.height == 1920 }
            ?: supportedCameraSizeList.firstOrNull { it.size.height == 720 }
    }

    /**
     * Setup a persistent [Surface] for the recorder
     * so we can use it as an output target for the
     * camera session without preparing the recorder
     */
    private val recorderSurface: Surface by lazy {

        // Get a persistent Surface from MediaCodec, don't forget to release when done
        val surface = MediaCodec.createPersistentInputSurface()

        // Prepare and release a dummy MediaRecorder with our new surface
        // Required to allocate an appropriately sized buffer before passing the Surface as the
        //  output target to the capture session
        createRecorder(surface).apply {
            prepare()
            release()
        }

        surface
    }

    /** Saves the video recording */
    private var recorder: MediaRecorder? = null

    /** [HandlerThread] where all camera operations run */
    private val cameraThread = HandlerThread("CameraThread").apply { start() }

    /** [Handler] corresponding to [cameraThread] */
    private val cameraHandler = Handler(cameraThread.looper)

    /** Where the camera preview is displayed */
    private lateinit var viewFinder: AutoFitSurfaceView

    /** Overlay on top of the camera preview */
    private lateinit var overlay: View

    /** Captures frames from a [CameraDevice] for our video recording */
    private lateinit var session: CameraCaptureSession

    /** The [CameraDevice] that will be opened in this fragment */
    private lateinit var camera: CameraDevice

    /** Requests used for preview only in the [CameraCaptureSession] */
    private val previewRequest: CaptureRequest by lazy {
        // Capture request holds references to target surfaces
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
            // Add the preview surface target
            addTarget(viewFinder.holder.surface)
        }.build()
    }

    /** Requests used for preview and recording in the [CameraCaptureSession] */
    private val recordRequest: CaptureRequest by lazy {
        // Capture request holds references to target surfaces
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {

            // Add the preview and recording surface targets
            addTarget(viewFinder.holder.surface)
            addTarget(recorderSurface)

            // Sets user requested FPS for all targets
            val fps = recommendedCameraInfo?.fps ?: 30
            set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, Range(fps, fps))
        }.build()
    }

    private var recordingStartMillis: Long = 0L

    /** Live data listener for changes in the device orientation relative to the camera */
    private lateinit var relativeOrientation: OrientationLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_record, container, false)

    /**
     *
     */
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.enterFullscreen(requireActivity())

        viewFinder = view.findViewById(R.id.view_finder)

        // TODO Question what does this do?
        viewFinder.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceDestroyed(holder: SurfaceHolder) = Unit
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) = Unit

            override fun surfaceCreated(holder: SurfaceHolder) {

                // Selects appropriate preview size and configures view finder
                val previewSize = getPreviewOutputSize(
                    viewFinder.display, characteristics, SurfaceHolder::class.java
                )
                Log.d(TAG, "View finder size: ${viewFinder.width} x ${viewFinder.height}")
                Log.d(TAG, "Selected preview size: $previewSize")
                viewFinder.setAspectRatio(previewSize.width, previewSize.height)

                // To ensure that size is set, initialize camera in the view's thread
                viewFinder.post { initializeCamera() }
            }
        })

        // Used to rotate the output media to match device orientation
        relativeOrientation = OrientationLiveData(requireContext(), characteristics).apply {
            observe(viewLifecycleOwner, Observer { orientation ->
                Log.d(TAG, "Orientation changed: $orientation")
            })
        }
    }

    /**
     * Creates a [MediaRecorder] instance using the
     * provided [Surface] as input
     */
    private fun createRecorder(surface: Surface) = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setVideoSource(MediaRecorder.VideoSource.SURFACE)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setOutputFile(outputFile.absolutePath)
        setVideoEncodingBitRate(RECORDER_VIDEO_BITRATE)
        recommendedCameraInfo?.let {
            setVideoSize(it.size.width, it.size.height)
            setVideoFrameRate(it.fps)
        }
        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setInputSurface(surface)
        setMaxDuration(10_000)
    }

    /**
     * Begin all camera operations in a coroutine in the main thread. This function:
     * - Opens the camera
     * - Configures the camera session
     * - Starts the preview by dispatching a repeating request
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initializeCamera() = lifecycleScope.launch(Dispatchers.Main) {

        // Open the selected camera
        camera = openCamera(cameraManager, args.cameraId, cameraHandler)

        // Creates list of Surfaces where the camera will output frames
        val targets = listOf(viewFinder.holder.surface, recorderSurface)

        // Start a capture session using our open camera and list of Surfaces where frames will go
        session = createCaptureSession(camera, targets, cameraHandler)

        // Sends the capture request as frequently as possible until the session is torn down or
        //  session.stopRepeating() is called
        session.setRepeatingRequest(previewRequest, null, cameraHandler)

        reset()

        // React to user touching the capture button
        capture_button.setOnClickListener {
            when (recording) {
                false -> start()
                true -> stop()
            }
        }
    }

    /**
     *  Resets the recording process.
     */
    private fun reset() = lifecycleScope.launch(Dispatchers.IO) {
        recorder = createRecorder(recorderSurface)
        prepare()
    }

    /**
     *  Prepares the recording process.
     */
    private fun prepare() = lifecycleScope.launch(Dispatchers.IO) {
        // Prevents screen rotation during the video recording
        requireActivity().requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LOCKED

        recorder?.apply {
            // Sets output orientation based on current sensor value at start time
            relativeOrientation.value?.let { setOrientationHint(it) }
            prepare()
        }

    }

    /**
     *  Starts the recording process.
     */
    private fun start() = lifecycleScope.launch(Dispatchers.IO) {
        // Start recording repeating requests, which will stop the ongoing preview
        //  repeating requests without having to explicitly call `session.stopRepeating`
        session.setRepeatingRequest(recordRequest, null, cameraHandler)

        // Finalizes recorder setup and starts recording
        recorder?.start()

        initView()

        recordingStartMillis = System.currentTimeMillis()
        Log.d(TAG, "Recording started")

    }.also {
        recording = true
    }

    /**
     *  Stops the recording process. This will launch a
     *  playback and confirmation popup.
     */
    private fun stop() = lifecycleScope.launch(Dispatchers.IO) {
        if (!recording) {
            cancel()
        }
        // Unlocks screen rotation after recording finished
        requireActivity().requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        // Requires recording of at least MIN_REQUIRED_RECORDING_TIME_MILLIS
        val elapsedTimeMillis = System.currentTimeMillis() - recordingStartMillis
        if (elapsedTimeMillis < MIN_REQUIRED_RECORDING_TIME_MILLIS) {
            delay(MIN_REQUIRED_RECORDING_TIME_MILLIS - elapsedTimeMillis)
        }

        Log.d(TAG, "Recording stopped. Output file: $outputFile")
        recorder?.stop()

        // Broadcasts the media file to the rest of the system
        MediaScannerConnection.scanFile(requireContext(), arrayOf(outputFile.absolutePath), null, null)

        val authority = "${BuildConfig.APPLICATION_ID}.provider"
        val localVideoUri = FileProvider.getUriForFile(requireContext(), authority, outputFile)

        // TODO THIS IS INCORRECT
        // TODO THIS IS INCORRECT
        // TODO THIS IS INCORRECT
        // TODO THIS IS INCORRECT
        // TODO THIS IS INCORRECT
        val localThumbnailUri = FileProvider.getUriForFile(requireContext(), authority, outputFile)

        lifecycleScope.launch(Dispatchers.Main) {
            stream_position_timer.stopTimer()

            // Confirmation and playback popup
            Dialog(requireActivity()).apply {
                setCancelable(true)
                setContentView(R.layout.video_confirm_dialogue)

                preview_container.setVideoURI(localVideoUri)
                preview_container.start()

                preview_cancel.setOnClickListener {
                    reset()
                    dismiss()
                }

                preview_ok.setOnClickListener {
                    dismiss()
                    upload(localVideoUri, localThumbnailUri)
                }
            }.show()
        }
    }.also {
        recording = false
    }

    /**
     *  Prepares the recording view.
     */
    private fun initView() = lifecycleScope.launch(Dispatchers.Main) {
        enableStopProgressBar.max =
            ((DEFAULT_MINIMUM_RECORD_TIME_MINUTES * 60) + DEFAULT_MINIMUM_RECORD_TIME_SECONDS) * 10

        stream_progress.max =
            ((DEFAULT_MAXIMUM_RECORD_TIME_MINUTES * 60) + DEFAULT_MAXIMUM_RECORD_TIME_SECONDS) * 10

        stream_max_duration.text =
            getString(
                R.string.timer_value,
                DEFAULT_MAXIMUM_RECORD_TIME_MINUTES,
                DEFAULT_MAXIMUM_RECORD_TIME_SECONDS
            )

        stream_position_timer.apply {
            addProgressBar(enableStopProgressBar) {
                // Allow broadcast to be stopped
                capture_button.isEnabled = true
                enableStopProgressBar.visibility = View.GONE
            }

            addProgressBar(stream_progress) {
                stop()
            }
        }

        stream_position_timer.apply {
            startTimer(stream_progress)
            text = getString(R.string.zero_time)
        }
        stream_progress.isIndeterminate = false
    }

    /**
     *  Called when the user confirms the reaction post after playback.
     *  This method includes success and failure callbacks.
     *
     *  @param localVideoUri Locally stored video file uri.
     *  @param localThumbnailUri Locally stored thumbnail file uri.
     */
    private fun upload(localVideoUri: Uri, localThumbnailUri: Uri) {
        lifecycleScope.launch(Dispatchers.Main) {
            reactionVm.postReaction(localVideoUri, localThumbnailUri, targetVlogId, true)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "The reaction has been posted!",
                                Toast.LENGTH_SHORT
                            ).show()
                            requireActivity().onBackPressed()
                        }
                    },
                    {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Failed to upload reaction, please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
        }
    }

    /** Opens the camera and returns the opened device (as the result of the suspend coroutine) */
    @SuppressLint("MissingPermission")
    private suspend fun openCamera(
        manager: CameraManager,
        cameraId: String,
        handler: Handler? = null
    ): CameraDevice = suspendCancellableCoroutine { cont ->
        askPermission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO) {
            manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(device: CameraDevice) = cont.resume(device)

                override fun onDisconnected(device: CameraDevice) {
                    Log.w(TAG, "Camera $cameraId has been disconnected")
                    requireActivity().onBackPressed()
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
                    if (cont.isActive) cont.resumeWithException(exc)
                }
            }, handler)
        }
    }

    /**
     * Creates a [CameraCaptureSession] and returns the configured session (as the result of the
     * suspend coroutine)
     */
    private suspend fun createCaptureSession(
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->

        // Creates a capture session using the predefined targets, and defines a session state
        // callback which resumes the coroutine once the session is configured
        device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e(TAG, exc.message, exc)
                cont.resumeWithException(exc)
            }
        }, handler)
    }

    override fun onStop() {
        super.onStop()
        try {
            camera.close()
        } catch (exc: Throwable) {
            Log.e(TAG, "Error closing camera", exc)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraThread.quitSafely()
        recorder?.release()
        recorderSurface.release()
    }

    companion object {
        private val TAG = RecordReactionFragment::class.java.simpleName

        private const val DEFAULT_MINIMUM_RECORD_TIME_SECONDS = 3
        private const val DEFAULT_MINIMUM_RECORD_TIME_MINUTES = 0
        private const val DEFAULT_MAXIMUM_RECORD_TIME_SECONDS = 10
        private const val DEFAULT_MAXIMUM_RECORD_TIME_MINUTES = 0

        private const val RECORDER_VIDEO_BITRATE: Int = 500_000
        private const val MIN_REQUIRED_RECORDING_TIME_MILLIS: Long = 1000L

        /** Milliseconds used for UI animations */
        const val ANIMATION_FAST_MILLIS = 50L
        const val ANIMATION_SLOW_MILLIS = 100L

        /** Creates a [File] named with the current date and time */
        private fun createFile(context: Context, extension: String): File {
            val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US)
            return File(context.filesDir, "VID_${sdf.format(Date())}.$extension")
        }

        private data class CameraInfo(
            val name: String,
            val cameraId: String,
            val size: Size,
            val fps: Int
        )

        /** Converts a lens orientation enum into a human-readable string */
        private fun lensOrientationString(value: Int) = when (value) {
            CameraCharacteristics.LENS_FACING_BACK -> "Back"
            CameraCharacteristics.LENS_FACING_FRONT -> "Front"
            CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
            else -> "Unknown"
        }

        private fun enumerateVideoCameras(cameraManager: CameraManager): List<CameraInfo> {
            val availableCameras: MutableList<CameraInfo> = mutableListOf()

            // Iterate over the list of cameras and add those with high speed video recording
            //  capability to our output. This function only returns those cameras that declare
            //  constrained high speed video recording, but some cameras may be capable of doing
            //  unconstrained video recording with high enough FPS for some use cases and they will
            //  not necessarily declare constrained high speed video capability.
            cameraManager.cameraIdList.forEach { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val orientation = lensOrientationString(
                    characteristics.get(CameraCharacteristics.LENS_FACING)!!
                )

                // Query the available capabilities and output formats
                val capabilities = characteristics.get(
                    CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES
                )!!
                val cameraConfig = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
                )!!

                // Return cameras that declare to be backward compatible
                if (capabilities.contains(
                        CameraCharacteristics
                            .REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
                    )
                ) {
                    // Recording should always be done in the most efficient format, which is
                    //  the format native to the camera framework
                    val targetClass = MediaRecorder::class.java

                    // For each size, list the expected FPS
                    cameraConfig.getOutputSizes(targetClass).forEach { size ->
                        // Get the number of seconds that each frame will take to process
                        val secondsPerFrame =
                            cameraConfig.getOutputMinFrameDuration(targetClass, size) /
                                1_000_000_000.0
                        // Compute the frames per second to let user select a configuration
                        val fps = if (secondsPerFrame > 0) (1.0 / secondsPerFrame).toInt() else 0
                        val fpsLabel = if (fps > 0) "$fps" else "N/A"
                        availableCameras.add(CameraInfo("$orientation ($id) $size $fpsLabel FPS", id, size, fps))
                    }
                }
            }

            return availableCameras
        }
    }

}
