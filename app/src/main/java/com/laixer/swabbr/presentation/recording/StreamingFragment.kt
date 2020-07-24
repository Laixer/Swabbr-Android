package com.laixer.swabbr.presentation.recording

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.laixer.swabbr.R
import com.laixer.swabbr.datasource.model.StreamResponse
import com.laixer.swabbr.datasource.model.getUrl
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import io.antmedia.android.broadcaster.ILiveVideoBroadcaster
import io.antmedia.android.broadcaster.LiveVideoBroadcaster
import io.antmedia.android.broadcaster.utils.OrientationLiveData
import kotlinx.android.synthetic.main.activity_app.*
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File
import java.io.Serializable
import java.net.ConnectException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class StreamingFragment : AuthFragment() {
    private val args: StreamingFragmentArgs by navArgs()
    private val vm: LivestreamViewModel by sharedViewModel()
    private val streamResponse: StreamResponse by lazy {
//        vm.startStreaming(args.streamRequest.livestreamId).blockingGet()
        StreamResponse(
            "",
            "",
            "livetest-debugams-euwe.channel.media.azure.net",
            1935,
            "live",
            "a29fe1e6cadb478c910b5a9ced9bdbd2",
            "",
            ""
        )
    }
    private lateinit var mLiveVideoBroadcaster: ILiveVideoBroadcaster

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectFeature()

        askPermission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO) {
            //all of your permissions have been accepted by the user
            // Hide parent UI and make the fragment fullscreen
            hideUI()

            surface_view.holder
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
            stop()
        }
    }

    private val cameraCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            try {
                this@StreamingFragment.lifecycleScope.launch(Dispatchers.Main) {
                    capture_button.isEnabled = false
                    capture_button.visibility = View.VISIBLE

                    timer_view.text = getString(R.string.connecting)

                }
                // mLiveVideoBroadcaster.connect(streamResponse.getUrl())
                mLiveVideoBroadcaster.connect(streamResponse.getUrl())
            } catch (e: ConnectException) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                stop()
            } finally {
                if (mLiveVideoBroadcaster.isConnected()) {
                    // We successfully connected, start countdown to broadcast
                    start()
                } else {
                    stop()
                    return
                }
            }
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.w(TAG, "Camera ${camera.id} has been disconnected")
            Toast.makeText(requireContext(), "Camera ${camera.id} disconnected.", Toast.LENGTH_LONG).show()
            stop()
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
            activity?.onBackPressed()
        }
    }

    private fun start() {
        lifecycleScope.launch(Dispatchers.Main) {
            timer_view.text = getString(R.string.get_ready)
            countDownFrom(COUNTDOWN_MILLISECONDS) {
                // Start broadcasting
                lifecycleScope.launch(Dispatchers.IO) {
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

    data class StreamRequest(
        val requestMoment: String,
        val requestTimeout: String,
        val livestreamId: String,
        val vlogId: String,
        val title: String,
        val message: String
    ) : Serializable

    companion object {

        private const val TAG = "CameraFragment"
        private const val COUNTDOWN_MILLISECONDS = 3_000L
        private const val COUNTDOWN_INTERVAL_MILLISECONDS = 1_000L
        private const val DEFAULT_MINIMUM_RECORD_TIME_SECONDS = 10
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
