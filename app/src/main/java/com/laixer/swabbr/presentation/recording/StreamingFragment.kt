package com.laixer.swabbr.presentation.recording

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.MediaCodec
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.laixer.swabbr.BuildConfig
import com.laixer.swabbr.R
import io.antmedia.android.broadcaster.ILiveVideoBroadcaster
import io.antmedia.android.broadcaster.LiveVideoBroadcaster
import io.antmedia.android.broadcaster.utils.OrientationLiveData
import kotlinx.android.synthetic.main.activity_app.*
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.Serializable
import java.net.ConnectException
import java.text.SimpleDateFormat
import java.util.*

open class StreamingFragment : Fragment() {
    private val args: StreamingFragmentArgs by navArgs()
    private val streamConfig: StreamConfig by lazy { /*args.streamConfig*/
        StreamConfig(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "livetest-debugams-euwe.channel.media.azure.net",
            1935,
            "live",
            "a29fe1e6cadb478c910b5a9ced9bdbd2",
            "",
            ""
        )
    }
    private lateinit var mLiveVideoBroadcaster: ILiveVideoBroadcaster

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        val context = requireContext().applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** Saves the video recording */
    private val recorder: MediaRecorder by lazy { createRecorder(recorderSurface) }

    /**
     * Setup a persistent [Surface] for the recorder so we can use it as an output target for the
     * camera session without preparing the recorder
     */
    private val recorderSurface: Surface by lazy {
        // Get a persistent Surface from MediaCodec, don't forget to release when done
        val surface: Surface = MediaCodec.createPersistentInputSurface()

        // Prepare and release a dummy MediaRecorder with our new surface
        // Required to allocate an appropriately sized buffer before passing the Surface as the
        //  output target to the capture session
        createRecorder(surface).apply {
            prepare()
            release()
        }

        surface
    }

    /** File where the recording will be saved */
    private val outputFile: File by lazy { createFile(requireContext(), "mp4") }

    /** Live data listener for changes in the device orientation relative to the camera */
    private lateinit var relativeOrientation: OrientationLiveData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        askPermission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO) {
            //all of your permissions have been accepted by the user
            // Hide parent UI and make the fragment fullscreen
            hideUI()
            // OpenGL ES 3.0 is supported from API >=18. Our minSdk is >=21, so this is safe to force.
            surface_view?.setEGLContextClientVersion(3)

            capture_button.isEnabled = false
            capture_button.setOnClickListener { stop() }

            enableStopProgressBar.max =
                ((DEFAULT_MINIMUM_RECORD_TIME_MINUTES * 60) + DEFAULT_MINIMUM_RECORD_TIME_SECONDS) * 10

            progress_bar.max =
                ((DEFAULT_MAXIMUM_RECORD_TIME_MINUTES * 60) + DEFAULT_MAXIMUM_RECORD_TIME_SECONDS) * 10

            timer_view.addProgressBar(enableStopProgressBar) {
                // Allow broadcast to be stopped
                capture_button.isEnabled = true
                enableStopProgressBar.visibility = View.GONE
            }

            timer_view.addProgressBar(progress_bar) {
                Toast.makeText(requireContext(), "Time limit reached, stopping broadcast.", Toast.LENGTH_LONG).show()
                stop()
            }

            timer_view.addEventAt(DEFAULT_MAXIMUM_RECORD_TIME_MINUTES - 1, DEFAULT_MAXIMUM_RECORD_TIME_SECONDS) {
                Toast.makeText(requireContext(), "One minute left!", Toast.LENGTH_LONG).show()
                timer_view.setTextColor(Color.RED)
            }

            // Used to rotate the output media to match device orientation
            relativeOrientation = OrientationLiveData(
                requireContext(),
                cameraManager.getCameraCharacteristics("0")
            )

            // Finalizes recorder setup
            recorder.apply {
                // Sets output orientation based on current sensor value at start time
                relativeOrientation.value?.let { setOrientationHint(it) }
                recorder.prepare()
            }

            mLiveVideoBroadcaster = LiveVideoBroadcaster(requireActivity(), surface_view, true, cameraCallback)

            if (mLiveVideoBroadcaster.canChangeCamera()) {
                switch_camera.visibility = View.VISIBLE
                switch_camera.setOnClickListener { mLiveVideoBroadcaster.changeCamera() }
            }

            if (mLiveVideoBroadcaster.canToggleTorch()) {
                toggle_torch.visibility = View.VISIBLE
                toggle_torch.setOnClickListener { mLiveVideoBroadcaster.toggleTorch() }
            }

            mLiveVideoBroadcaster.initializeCamera()

        }.onDeclined { e ->
            //at least one permission have been declined by the user
            Toast.makeText(requireContext(), "Unable to stream without permissions", Toast.LENGTH_LONG).show()
            requireActivity().onBackPressed()
        }
    }

    private val cameraCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            try {
                this@StreamingFragment.lifecycleScope.launch(Dispatchers.Main) {
                    capture_button.isEnabled = false
                    capture_button.visibility = View.VISIBLE

                    timer_view.text = getString(R.string.connecting)
                    // Used to rotate the output media to match device orientation
                    relativeOrientation.apply {
                        observe(viewLifecycleOwner, Observer { orientation ->
                            Log.d(TAG, "Orientation changed: $orientation")
                        })
                    }

                }

                // mLiveVideoBroadcaster.connect(streamConfig.getUrl())
                mLiveVideoBroadcaster.connect(streamConfig.getUrl())
            } catch (e: ConnectException) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                // TODO: Retry?
            } finally {
                if (mLiveVideoBroadcaster.isConnected()) {
                    // We successfully connected, start countdown to broadcast
                    start()
                } else {
                    // TODO: Abort
                    return
                }
            }
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.w(TAG, "Camera ${camera.id} has been disconnected")
            Toast.makeText(requireContext(), "Camera ${camera.id} disconnected.", Toast.LENGTH_LONG).show()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            val msg = "Camera error: " + when (error) {
                ERROR_CAMERA_DEVICE -> "Fatal (device)"
                ERROR_CAMERA_DISABLED -> "Device policy"
                ERROR_CAMERA_IN_USE -> "Camera in use"
                ERROR_CAMERA_SERVICE -> "Fatal (service)"
                ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                else -> "Unknown"
            }
            Log.e(TAG, msg)
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        }
    }

    private fun stop() {
        timer_view.stopTimer()
        lifecycleScope.launch(Dispatchers.Main) {
            // Clear the "keep screen on" flag
            if (mLiveVideoBroadcaster.isConnected()) {
                mLiveVideoBroadcaster.stopBroadcasting()
            }

            recorder.apply {
                stop()
                release()
            }
            // Broadcasts the media file to the rest of the system
            MediaScannerConnection.scanFile(
                requireContext(), arrayOf(outputFile.absolutePath), null, null
            )
            // Launch external activity via intent to play video recorded using our provider
            startActivity(Intent().apply {
                action = Intent.ACTION_VIEW
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(outputFile.extension)
                val authority = "${BuildConfig.APPLICATION_ID}.provider"
                data = FileProvider.getUriForFile(requireContext(), authority, outputFile)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }
    }

    private fun start() {
        lifecycleScope.launch(Dispatchers.Main) {
            timer_view.text = getString(R.string.get_ready)
            countDownFrom(COUNTDOWN_MILLISECONDS) {
                // Start broadcasting
                lifecycleScope.launch(Dispatchers.IO) {
                    // Finalizes recorder setup and starts recording
                    recorder.start()

                    mLiveVideoBroadcaster.startBroadcasting()

                }

                timer_view.text = getString(R.string.zero_time)
                progress_bar.isIndeterminate = false
                timer_view.startTimer(progress_bar)
            }
        }
    }

    private fun countDownFrom(countdownMs: Long, onFinish: suspend () -> Unit) {
        countdown.visibility = View.VISIBLE
        val timer = object : CountDownTimer(countdownMs, COUNTDOWN_INTERVAL_MILLISECONDS) {
            override fun onTick(millisUntilFinished: Long) {
                countdown.text = String.format("%1d", (millisUntilFinished / COUNTDOWN_INTERVAL_MILLISECONDS) + 1)
            }

            override fun onFinish() {
                countdown.visibility = View.INVISIBLE
                lifecycleScope.launch(Dispatchers.Main) { onFinish() }
            }
        }
        timer.start()
    }

    override fun onDestroy() {
        mLiveVideoBroadcaster.release()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requireActivity().toolbar.visibility = View.VISIBLE
        requireActivity().bottom_nav.visibility = View.VISIBLE
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

        recorder.release()
        recorderSurface.release()

        super.onDestroy()
    }

    private fun hideUI() {
        requireActivity().window.run {
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        requireActivity().toolbar.visibility = View.GONE
        requireActivity().bottom_nav.visibility = View.GONE
        activity?.window?.decorView?.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    fun StreamConfig.getUrl(): String = "rtmp://$hostServer:$hostPort/$applicationName/$streamKey/default"

    data class StreamConfig(
        val vlogId: UUID,
        val livestreamId: UUID,
        val hostServer: String,
        val hostPort: Int,
        val applicationName: String,
        val streamKey: String,
        val username: String,
        val password: String
    ) : Serializable

    /** Creates a [MediaRecorder] instance using the provided [Surface] as input */
    private fun createRecorder(surface: Surface) = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setVideoSource(MediaRecorder.VideoSource.SURFACE)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setOutputFile(outputFile.absolutePath)
        setVideoEncodingBitRate(RECORDER_VIDEO_BITRATE)
        setCaptureRate(30.0)
        setVideoFrameRate(30)
        setVideoSize(1080, 1920)
        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setInputSurface(surface)
    }

    companion object {
        private const val TAG = "CameraFragment"
        private const val COUNTDOWN_MILLISECONDS = 3_000L
        private const val COUNTDOWN_INTERVAL_MILLISECONDS = 1_000L
        private const val DEFAULT_MINIMUM_RECORD_TIME_SECONDS = 8
        private const val DEFAULT_MINIMUM_RECORD_TIME_MINUTES = 0
        private const val DEFAULT_MAXIMUM_RECORD_TIME_SECONDS = 0
        private const val DEFAULT_MAXIMUM_RECORD_TIME_MINUTES = 10
        private const val RECORDER_VIDEO_BITRATE: Int = 3_500_000

        /** Creates a [File] named with the current date and time */
        private fun createFile(context: Context, extension: String): File {
            val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US)
            return File(context.filesDir, "VID_${sdf.format(Date())}.$extension")
        }
    }
}
