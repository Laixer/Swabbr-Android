package com.laixer.swabbr.presentation.recording

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.hardware.camera2.*
import android.media.MediaCodec
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.media.ThumbnailUtils
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Range
import android.util.Size
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.MainActivity
import com.laixer.swabbr.utils.*
import io.antmedia.android.broadcaster.utils.OrientationLiveData
import kotlinx.android.synthetic.main.fragment_record_video.*
import kotlinx.coroutines.*
import java.io.File
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// TODO This currently does not support a min/max record time of 0/infinite.
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
 *
 *  This expects the required permissions to already be granted. A
 *  check is done at the [onCreate] method.
 */
open class RecordVideoFragment : Fragment() {
    private var recording = false

    /**
     *  Native Android camera manager instance.
     */
    private val cameraManager: CameraManager by lazy {
        val context = requireContext().applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /**
     *  Gets the first camera in the camera id list of the [CameraManager].
     */
    private val characteristics: CameraCharacteristics by lazy {
        val list = cameraManager.cameraIdList
        cameraManager.getCameraCharacteristics(list.first())
    }

    /**
     * File where the recording will be saved.
     */
    protected val videoFile: File by lazy { FileHelper.createFile(requireContext(), "mp4") }

    /**
     *  File where the recording thumbnail will be saved.
     */
    protected val thumbnailFile: File by lazy { FileHelper.createFile(requireContext(), "jpeg") }

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

    /**
     *  Object required to be able to capture camera frames.
     */
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

            // TODO What to do with FPS?
            // Sets user requested FPS for all targets
            set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, Range(30, 30))
        }.build()
    }

    private var recordingStartMillis: Long = 0L

    /** Live data listener for changes in the device orientation relative to the camera */
    private lateinit var relativeOrientation: OrientationLiveData

    /**
     *  This checks if we have the required permissions. If this
     *  is not the case, the permissions are requested. If any of
     *  these are denied, we go back to the previous activity or
     *  fragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askPermission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO) {
            // Do nothing
        }.onDeclined {
            // At least one permission has been declined by the user
            showMessage("Your permissions are required to record videos")

            // Go back to home.
            val intent = Intent(this.context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        injectFeature()


    }

    /**
     *  Inflate a generic recording fragment.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_record_video, container, false)

    /**
     *  Sets up our UI. Note that if we don't have camera permissions
     *  this will simulate a back press along with a displayed message.
     *
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.enterFullscreen(requireActivity())

        askPermission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO) {
            // Set some automatic time-based events for our TimerView
            stream_position_timer.apply {
                // Allow broadcast to be stopped when the circular progress
                // bar completes and thus exceeds the minimum recording time.
                // The circular progress bar disappears also.
                addProgressBar(circular_progress_bar) {
                    capture_button?.isEnabled = true
                    circular_progress_bar?.visibility = View.GONE
                }

                // Stop the recording when the time limit is exceeded.
                addProgressBar(horizontal_progress_bar) {
                    showMessage("Time limit reached, stopping recording.")
                    stopRecording()
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
                    false -> startRecording()
                    true -> stopRecording()
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
            showMessage("Your permissions are required to record videos")

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
        setVideoEncodingBitRate(2_000_000)
        setAudioEncodingBitRate(192_000)
        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setInputSurface(surface)
        setMaxDuration(10_000) // TODO Hardcoded max duration......

        // TODO Width and height?
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
        // TODO Camera id
        // Open the selected camera
        val cameraId = cameraManager.cameraIdList.first()
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
    protected open fun startRecording() {
        // TODO Preconditions!!!

        // Update IO related objects
        lifecycleScope.launch(Dispatchers.IO) {
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
    }

    /**
     *  Stops the recording process. This function can be overridden to
     *  apply custom functionality to the recording process. This also
     *  stops the timer, removing all registered events.
     */
    protected open fun stopRecording() {
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
            val thumbnail = ThumbnailUtils.createVideoThumbnail(videoFile, Size(384, 512), cancellationSignal)
            FileHelper.writeBitmapToFile(thumbnail, thumbnailFile)

        }.also {
            recording = false
        }
    }

    /**
     *  Opens the camera and returns the opened device as the
     *  result of the suspend coroutine).
     */
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

                    findNavController().popBackStack()
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
     *  Creates a [CameraCaptureSession] and returns the configured
     *  session as the result of the suspend coroutine.
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

    /**
     *  When we stop the current fragment try to close the camera.
     */
    override fun onStop() {
        super.onStop()

        try {
            camera.close()
        } catch (e: Throwable) {
            Log.e(TAG, "Error closing camera", e)
        }
    }

    /**
     *  Cleans up all our resources.
     */
    override fun onDestroy() {
        super.onDestroy()
        cameraThread.quitSafely()
        recorder?.release()
        recorderSurface.release()
    }

    companion object {
        private val TAG = RecordVideoFragment::class.java.simpleName

        // TODO Double declaration
        private val DEFAULT_MINIMUM_RECORD_TIME = Duration.ofSeconds(3)
        private val DEFAULT_MAXIMUM_RECORD_TIME = Duration.ofSeconds(10)

        private const val RECORDER_VIDEO_BITRATE: Int = 2_000_000
        private const val MIN_REQUIRED_RECORDING_TIME_MILLIS: Long = 1000L
    }
}
