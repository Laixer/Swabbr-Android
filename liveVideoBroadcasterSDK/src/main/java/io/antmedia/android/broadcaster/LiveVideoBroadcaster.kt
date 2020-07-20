package io.antmedia.android.broadcaster

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.AudioFormat
import android.media.AudioRecord
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.util.Log
import android.util.Range
import android.view.Surface
import android.widget.Toast
import androidx.core.app.ActivityCompat
import io.antmedia.android.R
import io.antmedia.android.broadcaster.CameraHandler.ICameraViewer
import io.antmedia.android.broadcaster.encoder.AudioHandler
import io.antmedia.android.broadcaster.encoder.CameraSurfaceRenderer
import io.antmedia.android.broadcaster.encoder.TextureMovieEncoder
import io.antmedia.android.broadcaster.encoder.VideoEncoderCore
import io.antmedia.android.broadcaster.network.IMediaMuxer
import io.antmedia.android.broadcaster.network.RTMPStreamer
import io.antmedia.android.broadcaster.utils.OrientationLiveData
import io.antmedia.android.broadcaster.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.ConnectException
import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Created by mekya on 28/03/2017.
 */
class LiveVideoBroadcaster(
    private var context: Activity,
    private var mGLView: GLSurfaceView,
    private var adaptiveStreamingEnabled: Boolean = false,
    private var recordingSurface: Surface,
    private var cameraCallback: CameraDevice.StateCallback
) :
    ILiveVideoBroadcaster, ICameraViewer, CoroutineScope, SurfaceTexture.OnFrameAvailableListener {

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val _manager: CameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    private var _cameraId: String = CAMERA_BACK
    private lateinit var mGLViewSurface: Surface

    /** [CameraCharacteristics] corresponding to the provided Camera ID */
    private val _characteristics: CameraCharacteristics by lazy {
        _manager.getCameraCharacteristics(_camera.id)
    }
    private val mRtmpHandlerThread: HandlerThread =
        HandlerThread("RtmpStreamerThread").apply { start() }//, Process.THREAD_PRIORITY_BACKGROUND);
    private val mRtmpStreamer: IMediaMuxer = RTMPStreamer(mRtmpHandlerThread.looper)

    /** [HandlerThread] where all camera operations run */
    private val cameraThread = HandlerThread("CameraThread").apply { start() }

    /** [Handler] corresponding to [cameraThread] */
    private val cameraHandler = Handler(cameraThread.looper)

    /** [HandlerThread] where all camera operations run */
    private val backgroundThread = HandlerThread("BackgroundThread").apply { start() }

    /** [Handler] corresponding to [backgroundThread] */
    private val backgroundHandler = Handler(backgroundThread.looper)

    /** Captures frames from a [CameraDevice] for our video recording */
    private lateinit var session: CameraCaptureSession

    /** The [CameraDevice] that will be opened in this fragment */
    private lateinit var _camera: CameraDevice

    /** Requests used for preview only in the [CameraCaptureSession] */
    private val previewRequest: CaptureRequest by lazy {
        // Capture request holds references to target surfaces
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
            // Add the preview surface target
            addTarget(mGLViewSurface)
        }.build()
    }

    /** Requests used for recording in the [CameraCaptureSession] */
    private val recordRequest: CaptureRequest by lazy {
        // Capture request holds references to target surfaces
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
            // Add the preview and recording surface targets
            addTarget(mGLViewSurface)
            // Sets user requested FPS for all targets
            set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, Range(frameRate, frameRate))
        }.build()
    }

    /** Live data listener for changes in the device orientation relative to the camera */
    private lateinit var relativeOrientation: OrientationLiveData

    /** [HandlerThread] where all audio operations run */
    private var audioHandlerThread: HandlerThread =
        HandlerThread("AudioHandlerThread", Process.THREAD_PRIORITY_AUDIO).apply { start() }

    /** [Handler] corresponding to [audioHandlerThread] */
    private var audioHandler: AudioHandler = AudioHandler(audioHandlerThread.looper)
    private var audioThread: AudioRecorderThread? = null
    private var isRecording = false
    private var mCameraHandler: CameraHandler = CameraHandler(this)
    private var mRenderer: CameraSurfaceRenderer = CameraSurfaceRenderer(mCameraHandler, sVideoEncoder)
    private val frameRate = 60
    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var adaptiveStreamingTimer: Timer? = null

    init {
        mGLView.setRenderer(mRenderer)
        mGLView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override fun isConnected(): Boolean = mRtmpStreamer.isConnected

    override fun setAdaptiveStreaming(enable: Boolean) {
        adaptiveStreamingEnabled = enable
    }

    override fun pause() {
        //first making mGLView GONE is important otherwise
        //camera function is called after release exception may be thrown
        //especially in htc one x 4.4.2
        stopBroadcasting()
    }

    override fun release() {
        audioHandlerThread.quitSafely()
        mRtmpHandlerThread.quitSafely()
        mCameraHandler.invalidateHandler()
    }

    @Throws(ConnectException::class)
    override fun connect(rtmpUrl: String) {
        require(hasConnection()) { "No active network connection found" }
        if (!mRtmpStreamer.open(rtmpUrl)) {
            throw ConnectException("Unable to connect socket to remote address")
        }
    }

    @Throws(
        IllegalArgumentException::class,
        CameraAccessException::class,
        IllegalThreadStateException::class
    )
    override fun startBroadcasting() = launch(Dispatchers.IO) {
        require(!isRecording) { "can't start broadcasting when already live" }
        require(isConnected()) { "startBroadcasting cannot be called before connect" }
        require(Utils.doesEncoderWork(context) == Utils.ENCODER_WORKS) { "This device does not support hardware encoders" }

        val recordStartTime = System.currentTimeMillis()
        // Prevents screen rotation during the video recording
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        // Start recording repeating requests, which will stop the ongoing preview
        // repeating requests without having to explicitly call `session.stopRepeating`
        session.setRepeatingRequest(recordRequest, null, cameraHandler)

        mRenderer.setOptions(mRtmpStreamer)
        // notify the renderer that we want to change the encoder's state
        mRenderer.startRecording(recordStartTime)
        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_AUDIO_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        // Start the audio encoder in a separate thread
        audioHandler.startAudioEncoder(mRtmpStreamer, SAMPLE_AUDIO_RATE_IN_HZ, minBufferSize)
        audioThread =
            AudioRecorderThread(SAMPLE_AUDIO_RATE_IN_HZ, recordStartTime, audioHandler).apply { start() }
        isRecording = true
        if (adaptiveStreamingEnabled) {
            checkBitrate()
        }
    }

    private fun checkBitrate() {
        adaptiveStreamingTimer = Timer().apply {
            schedule(object : TimerTask() {
                var previousFrameCount = 0
                var frameQueueIncreased = 0

                override fun run() {
                    val frameCountInQueue = mRtmpStreamer.videoFrameCountInQueue
                    Log.d(TAG, "video frameCountInQueue : $frameCountInQueue")
                    if (frameCountInQueue > previousFrameCount) {
                        frameQueueIncreased++
                    } else {
                        frameQueueIncreased--
                    }
                    previousFrameCount = frameCountInQueue
                    if (frameQueueIncreased > 10) {
                        //decrease bitrate
                        println("decrease bitrate")
                        var frameRate = mRenderer.frameRate
                        if (frameRate >= 13) {
                            frameRate -= 3
                            mRenderer.frameRate = frameRate
                        } else {
                            if (mRenderer.bitrate > 2_000_000) { //2Mbit
                                mRenderer.bitrate -= 100_000
                                // notify the renderer that we want to change the encoder's state
                                mRenderer.recorderConfigChanged()
                            }
                        }

                        frameQueueIncreased = 0
                    }
                    if (frameQueueIncreased < -10) {
                        //increase bitrate
                        println("//increase bitrate")
                        if (mRenderer.frameRate <= frameRate - 3) {
                            mRenderer.frameRate += 3
                        } else {
                            var bitrate = mRenderer.bitrate
                            if (bitrate < 2_000_000) { //2Mbit
                                bitrate += 100_000
                                mRenderer.bitrate = bitrate
                                // notify the renderer that we want to change the encoder's state
                                mRenderer.recorderConfigChanged()
                            }
                        }
                        frameQueueIncreased = 0
                    }
                }
            }, 0, 500)
        }
    }

    @Throws(IllegalArgumentException::class)
    override fun stopBroadcasting() = launch(Dispatchers.IO) {
        require(isRecording) { "can't stop broadcast, broadcast was never started" }
        // Stop the renderer
        mRenderer.stopRecording()
        // Stop the adaptive stream timer
        adaptiveStreamingTimer?.cancel()
        adaptiveStreamingTimer = null
        // Stop audio processing
        audioThread?.stopAudioRecording()
        audioHandler.sendEmptyMessage(AudioHandler.END_OF_STREAM)

        try {
            // Give the recorder 250ms to stop recording, otherwise force stop
            for (i in 1..5) {
                if (sVideoEncoder.isRecording) {
                    Thread.sleep(50)
                }
            }
        } catch (e: InterruptedException) {
            sVideoEncoder.stopRecording()
            Log.e(TAG, e.message!!)
        }
        // Force stop
        sVideoEncoder.stopRecording()
    }

    override fun handleSetSurfaceTexture(st: SurfaceTexture) {
        st.setOnFrameAvailableListener(this)
        mGLViewSurface = Surface(st)
        initializeCamera()
    }

    /**
     * Begin all camera operations in a coroutine in the main thread. This function:
     * - Opens the camera
     * - Configures the camera session
     * - Starts the preview by dispatching a repeating request
     */
    override fun initializeCamera() = launch(Dispatchers.Main) {
        // Open the selected camera
        mRenderer.setCameraPreviewSize(mGLView.measuredWidth, mGLView.measuredHeight)
        _camera = openCamera(_manager, _cameraId, backgroundHandler, cameraCallback)
        // Start a preview capture session using our open camera and list of Surfaces where frames will go
        session = createCaptureSession(_camera, listOf(mGLViewSurface), cameraHandler)
        // Sends the capture request as frequently as possible until the session is torn down or
        //  session.stopRepeating() is called
        session.setRepeatingRequest(previewRequest, null, cameraHandler)
        // Used to rotate the output media to match device orientation
        relativeOrientation = OrientationLiveData(context, _characteristics)
    }

    /** Opens the camera and returns the opened device (as the result of the suspend coroutine) */
    private suspend fun openCamera(
        manager: CameraManager, cameraId: String, handler: Handler? = null, cameraCallback: CameraDevice.StateCallback
    ): CameraDevice = suspendCancellableCoroutine { cont ->
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            with(RuntimeException("No permission granted")) {
                Log.e(TAG, this.message, this)
                cont.resumeWithException(this)
            }
        }

        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) = cont.resume(device).also {
                if (Utils.doesEncoderWork(context) == Utils.ENCODER_NOT_TESTED) {
                    val encoderWorks = VideoEncoderCore.doesEncoderWork(
                        mGLView.measuredWidth,
                        mGLView.measuredHeight,
                        3_000_000,
                        frameRate
                    )
                    Utils.setEncoderWorks(context, encoderWorks)
                }
                cameraCallback.onOpened(device)
            }

            override fun onDisconnected(device: CameraDevice) =
                cont.resume(device).also { cameraCallback.onDisconnected(device) }

            override fun onClosed(device: CameraDevice) = cont.resume(device).also { cameraCallback.onClosed(device) }

            override fun onError(device: CameraDevice, error: Int) {
                val msg = when (error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }
                with(RuntimeException("Camera $cameraId error: ($error) $msg")) {
                    Log.e(TAG, this.message, this)
                    if (cont.isActive) cont.resumeWithException(this).also { cameraCallback.onError(device, error) }
                }
            }
        }, handler)
    }

    /**
     * Creates a [CameraCaptureSession] and returns the configured session (as the result of the
     * suspend coroutine)
     */
    private suspend fun createCaptureSession(
        device: CameraDevice, targets: List<Surface>, handler: Handler? = null
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

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) = mGLView.requestRender()

    private fun hasConnection(): Boolean =
        connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false

    override fun changeCamera() {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Toast.makeText(context, R.string.only_one_camera_exists, Toast.LENGTH_LONG).show()
            return
        }
        // TODO - Swap camera
//        object : AsyncTask<Void?, Void?, Camera.Parameters?>() {
//            override fun onPreExecute() {
//                super.onPreExecute()
//                mGLView!!.queueEvent { // Tell the renderer that it's about to be paused so it can clean up.
//                    mRenderer!!.notifyPausing()
//                }
//                mGLView!!.onPause()
//                mGLView!!.setOnTouchListener(null)new CameraSurfaceRenderer(mCameraHandler, sVideoEncoder);
//            }
//
//            protected override fun doInBackground(vararg voids: Void): Camera.Parameters? {
//                releaseCamera()
//                try {
//                    sCameraProxy = CameraProxy(currentCameraId)
//                    val parameters: Camera.Parameters = sCameraProxy.getParameters()
//                    if (parameters != null) {
//                        setCameraParameters(parameters)
//                        return parameters
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                return null
//            }
//
//            override fun onPostExecute(parameters: Camera.Parameters?) {
//                super.onPostExecute(parameters)
//                if (parameters != null) {
//                    mGLView!!.onResume()
//                    setRendererPreviewSize()
//                } else {
//                    Snackbar.make(mGLView!!, R.string.camera_not_running_properly, Snackbar.LENGTH_LONG)
//                            .show()
//                }
//            }
//        }.execute()
    }

    companion object {
        private val TAG = LiveVideoBroadcaster::class.java.simpleName
        private const val CAMERA_FRONT = "1"
        private const val CAMERA_BACK = "0"

        @Volatile
        private var sCameraReleased = false
        const val PERMISSIONS_REQUEST = 8954
        const val SAMPLE_AUDIO_RATE_IN_HZ = 44100
        private val sVideoEncoder = TextureMovieEncoder()
    }

    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext
}
