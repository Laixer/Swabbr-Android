package com.laixer.swabbr.presentation.recording

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.hardware.camera2.*
import android.media.MediaCodec
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Range
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.MainActivity
import com.laixer.swabbr.utils.*
import io.antmedia.android.broadcaster.utils.OrientationLiveData
import kotlinx.android.synthetic.main.fragment_record_video.*
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import android.media.ThumbnailUtils
import android.os.CancellationSignal
import java.io.Console
import java.io.FileOutputStream
import java.time.LocalDateTime

// TODO This currently does not support a min/max record time of 0/inifinite.
// TODO Question - when do we exit fullscreen explicitly?
// TODO Pressing back while recording crashes the app.
/**
 *  Fragment containing functionality for recording video files. This
 *  can be extended to use this functionality to record reactions or
 *  vlogs, or some other kind of video.
 *
 *  This activity uses the camera/camcorder as the A/V source for the
 *  [android.media.MediaRecorder] API. A [android.view.TextureView]
 *  is used as the camera preview which limits the code to API 14+.
 *  This can be easily replaced with a [android.view.SurfaceView] to
 *  run on older devices.
 */
open class RecordVideoFragment : Fragment() {
    private var recording = false

    // TODO Fix
    /**
     *  The camera id is 0, 1, ... which should be initialized somewhere.
     *  The old method was passing it through a hardcoded navigation arg.
     *  In the future the camera id should be passed through navigation
     *  args again and should be modifiable. This is a tempfix.
     */
    private val cameraId = "1"

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        val context = requireContext().applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** [CameraCharacteristics] corresponding to the provided Camera ID */
    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics(cameraId)
    }

    /**
     * File where the recording will be saved.
     */
    protected val videoFile: File by lazy { createFile(requireContext(), "mp4") }

    /**
     *  File where the recording thumbnail will be saved.
     */
    protected val thumbnailFile: File by lazy { createFile(requireContext(), "jpeg") }

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

    /**
     *  Inflate a generic recording fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_record_video, container, false)

    /**
     *  Sets up the view. After going fullscreen and asking permission, this
     *  - Sets up the circular progress bar.
     *  - Sets up the horizontal progress bar.
     *  - Implements button click listeners.
     *  - Calls [initMinMaxVideoTimes] with default values.
     *
     *  If any permissions are declined, this will go to the previous activity.
     */
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.enterFullscreen(requireActivity())

        // Ask permissions, then continue, else return.
        askPermission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO) {
            // Set some automatic time-based events for our TimerView
            stream_position_timer.apply {
                // Allow broadcast to be stopped when the circular progress
                // bar completes and thus exceeds the minimum recording time.
                // The circular progress bar disappears also.
                addProgressBar(circular_progress_bar) {
                    capture_button.isEnabled = true
                    circular_progress_bar.visibility = View.GONE
                }

                // Stop the recording when the time limit is exceeded.
                addProgressBar(horizontal_progress_bar) {
                    Toast.makeText(
                        requireContext(),
                        "Time limit reached, stopping recording.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    stop()
                }
            }

            switch_camera.apply {
                visibility = View.VISIBLE
                // TODO
                //setOnClickListener { this.changeCamera() }
            }

            toggle_torch.apply {
                visibility = View.VISIBLE
                // TODO
                //setOnClickListener { mLiveVideoBroadcaster.toggleTorch() }
            }

            // React to user touching the capture button
            // TODO Move this back to initializeCamera() to prevent race condition?
            capture_button.setOnClickListener {
                when (recording) {
                    false -> start()
                    true -> stop()
                }
            }

            // Initialize the camera when the surface view has loaded.
            viewFinder = view.findViewById(R.id.view_finder)
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
                    // TODO This is a race condition. If this isn't called before start(), the app will crash.
                    viewFinder.post { initializeCamera() }
                }
            })

            // Used to rotate the output media to match device orientation
            relativeOrientation = OrientationLiveData(requireContext(), characteristics).apply {
                observe(viewLifecycleOwner, Observer { orientation ->
                    Log.d(TAG, "Orientation changed: $orientation")
                })
            }

            // This will setup the default minimum and maximum times.
            // These can be overridden before starting the recording process.
            initMinMaxVideoTimes(DEFAULT_MINIMUM_RECORD_TIME, DEFAULT_MAXIMUM_RECORD_TIME)

        }.onDeclined {
            // At least one permission has been declined by the user
            Toast.makeText(requireContext(), "Unable to vlog without permissions", Toast.LENGTH_LONG).show()

            // Go back to home.
            val intent = Intent(this.context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    /**
     *  Resets the recording process.
     */
    protected fun reset() = lifecycleScope.launch(Dispatchers.IO) {
        // Create a new recorder each time.
        recorder = createRecorder(recorderSurface)

        // Prevents screen rotation during the video recording
        requireActivity().requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LOCKED

        recorder?.apply {
            // Sets output orientation based on current sensor value at start time
            relativeOrientation.value?.let { setOrientationHint(it) }
            prepare()
        }
    }.also {
        lifecycleScope.launch(Dispatchers.Main) {
            // Go to full screen. Looks better and else we hide some UI elements.
            Utils.enterFullscreen(requireActivity())

            // The horizontal progress bar should display no progress yet.
            horizontal_progress_bar.progress = 0
        }
    }

    /**
     *  Function for setting minimum and maximum recording times.
     *  This will:
     *  - Set the max for the [circular_progress_bar].
     *  - Set time max for [horizontal_progress_bar].
     *  - Set text for [stream_max_duration].
     *
     *  Note that this does not setup callbacks or starts the timer.
     *
     *  @param minimumRecordTime Minimum recording time as duration.
     *  @param maximumRecordTime Maximum recording time as duration.
     *  @throws [IllegalStateException] if we are already recording.
     */
    protected fun initMinMaxVideoTimes(minimumRecordTime: Duration, maximumRecordTime: Duration) {
        if (recording) {
            throw IllegalStateException("Can't modify minimum and maximum time during recording")
        }

        lifecycleScope.launch(Dispatchers.Main) {
            // Setup the circular progress bar that enables the stop button.
            // TODO Dangerous cast
            circular_progress_bar.max = minimumRecordTime.seconds.toInt() * 10 // TODO Why x10?

            // Setup the horizontal progress bar at the bottom of the screen.
            // TODO Dangerous cast
            horizontal_progress_bar.max = maximumRecordTime.seconds.toInt() * 10 // TODO Why x10?

            // Set the maximum duration text
            stream_max_duration.text =
                getString(
                    R.string.timer_value,
                    maximumRecordTime.minutes(),
                    maximumRecordTime.lastMinuteSeconds()
                )
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
        setOutputFile(videoFile.absolutePath)
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
     *  Begin all camera operations in a coroutine in the main
     *  thread. This function:
     *  - Opens the camera.
     *  - Configures the camera session.
     *  - Starts the preview by dispatching a repeating request.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initializeCamera() = lifecycleScope.launch(Dispatchers.Main) {
        // TODO Remove
        println("Called initializeCamera() at ${LocalDateTime.now()}")

        // Open the selected camera
        camera = openCamera(cameraManager, cameraId, cameraHandler)

        // Creates list of Surfaces where the camera will output frames
        val targets = listOf(viewFinder.holder.surface, recorderSurface)

        // Start a capture session using our open camera and list of Surfaces where frames will go
        session = createCaptureSession(camera, targets, cameraHandler)

        // Sends the capture request as frequently as possible until the session is torn down or
        //  session.stopRepeating() is called
        session.setRepeatingRequest(previewRequest, null, cameraHandler)

        reset()
    }

    /**
     *  Starts the recording process. This function can be overridden
     *  to apply custom functionality to the recording process. This
     *  also makes the record/stop button visible (but disabled) and
     *  starts the horizontal progress bar.
     */
    protected open fun start() {
        // Update IO related objects
        lifecycleScope.launch(Dispatchers.IO) {

            // TODO Remove
            println("Called start() at ${LocalDateTime.now()}")

            // Start recording repeating requests, which will stop the ongoing preview
            //  repeating requests without having to explicitly call `session.stopRepeating`
            session.setRepeatingRequest(recordRequest, null, cameraHandler)

            // Finalizes recorder setup and starts recording
            recorder?.start()

            recordingStartMillis = System.currentTimeMillis()
            Log.d(TAG, "Recording started")
        }.also {
            recording = true
        }

        // Update UI
        lifecycleScope.launch(Dispatchers.Main) {
            // Un-hide the circular progress bar if it was hidden.
            // This is relevant when resetting the process.
            circular_progress_bar.visibility = View.VISIBLE

            // Start the timer for all progress bars.
            stream_position_timer.apply {
                startTimer()
                text = getString(R.string.zero_time)
            }
            horizontal_progress_bar.isIndeterminate = false

            // Make the stop button visible again but keep it disabled.
            // Note that the circular progress bar will enable it again
            // when the minimum vlogging time has passed.
            capture_button.apply {
                isEnabled = false
                visibility = View.VISIBLE
            }
        }
    }

    /**
     *  Stops the recording process. This function can be overridden to
     *  apply custom functionality to the recording process. This also
     *  stops the timer, removing all registered events.
     */
    protected open fun stop() {
        lifecycleScope.launch(Dispatchers.IO) {
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

            Log.d(TAG, "Recording stopped. Output file: $videoFile")
            recorder?.stop()

            // Broadcasts the media file to the rest of the system
            MediaScannerConnection.scanFile(requireContext(), arrayOf(videoFile.absolutePath), null, null)

            // Generates the thumbnail
            val cancellationSignal = CancellationSignal() // TODO Use.
            val thumbnail = ThumbnailUtils.createVideoThumbnail(videoFile, Size(384,512), cancellationSignal)

            // TODO Put in a helper.
            val os = FileOutputStream(thumbnailFile)
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()

        }.also {
            recording = false
        }

        // Update UI components.
        lifecycleScope.launch(Dispatchers.Main) {
            // Stop the timer, preventing upcoming timer events.
            // Don't reset the events so we can re-use the timer.
            stream_position_timer.stopTimer(resetEvents = false)
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
        private val TAG = RecordVideoFragment::class.java.simpleName

        private val DEFAULT_MINIMUM_RECORD_TIME = Duration.ofSeconds(3)
        private val DEFAULT_MAXIMUM_RECORD_TIME = Duration.ofSeconds(10)

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
