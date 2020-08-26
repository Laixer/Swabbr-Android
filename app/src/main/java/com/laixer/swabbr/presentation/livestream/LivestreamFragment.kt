package com.laixer.swabbr.presentation.livestream

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.hardware.camera2.CameraDevice
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.data.datasource.model.StreamResponse
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.Utils.enterFullscreen
import com.laixer.swabbr.presentation.Utils.exitFullscreen
import io.antmedia.android.broadcaster.ILiveVideoBroadcaster
import io.antmedia.android.broadcaster.LiveVideoBroadcaster
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

open class LivestreamFragment : AuthFragment() {

    private val args: LivestreamFragmentArgs by navArgs()
    private val vm: LivestreamViewModel by sharedViewModel()
    private lateinit var mLiveVideoBroadcaster: ILiveVideoBroadcaster

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectFeature()
        vm.streamResponse.observe(viewLifecycleOwner, Observer { connect(it) })
        //all of your permissions have been accepted by the user
        // Hide parent UI and make the fragment fullscreen
        enterFullscreen(requireActivity())
        // OpenGL ES 3.0 is supported from API >=18. Our minSdk is >=21, so this is safe to force.
        surface_view.setEGLContextClientVersion(3)

        capture_button.apply {
            isEnabled = false
            setOnClickListener { stop() }
        }

        enableStopProgressBar.max =
            ((DEFAULT_MINIMUM_RECORD_TIME_MINUTES * 60) + DEFAULT_MINIMUM_RECORD_TIME_SECONDS) * 10

        stream_progress.max =
            ((DEFAULT_MAXIMUM_RECORD_TIME_MINUTES * 60) + DEFAULT_MAXIMUM_RECORD_TIME_SECONDS) * 10

        stream_max_duration.text =
            getString(R.string.timer_value, DEFAULT_MAXIMUM_RECORD_TIME_MINUTES, DEFAULT_MAXIMUM_RECORD_TIME_SECONDS)

        stream_position_timer.apply {
            addProgressBar(enableStopProgressBar) {
                // Allow broadcast to be stopped
                capture_button.isEnabled = true
                enableStopProgressBar.visibility = View.GONE
            }

            addProgressBar(stream_progress) {
                Toast.makeText(requireContext(), "Time limit reached, stopping broadcast.", Toast.LENGTH_LONG).show()
                stop()
            }

            addEventAt(DEFAULT_MAXIMUM_RECORD_TIME_MINUTES - 1, DEFAULT_MAXIMUM_RECORD_TIME_SECONDS) {
                Toast.makeText(requireContext(), "One minute left!", Toast.LENGTH_LONG).show()
                setTextColor(Color.RED)
            }
        }

        initialize()
    }

    private fun initialize() = askPermission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO) {
        mLiveVideoBroadcaster = LiveVideoBroadcaster(requireActivity(), surface_view, true, cameraCallback)

        if (mLiveVideoBroadcaster.canChangeCamera()) {
            switch_camera.apply {
                visibility = View.VISIBLE
                setOnClickListener { mLiveVideoBroadcaster.changeCamera() }
            }
        }

        if (mLiveVideoBroadcaster.canToggleTorch()) {
            toggle_torch.apply {
                visibility = View.VISIBLE
                setOnClickListener { mLiveVideoBroadcaster.toggleTorch() }
            }
        }

        mLiveVideoBroadcaster.initializeCamera()
    }.onDeclined { e ->
        //at least one permission have been declined by the user
        Toast.makeText(requireContext(), "Unable to stream without permissions", Toast.LENGTH_LONG).show()
        stop()
    }

    private val cameraCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            this@LivestreamFragment.lifecycleScope.launch(Dispatchers.Main) {
                status_text.text = getString(R.string.retrieving_credentials)
            }
            vm.startStreaming(args.livestreamId)
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
            stop()
        }
    }

    private fun connect(res: Resource<StreamResponse>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {
                this@LivestreamFragment.lifecycleScope.launch(Dispatchers.Main) {
                    capture_button.apply {
                        isEnabled = false
                        visibility = View.VISIBLE
                    }

                    status_text.text = getString(R.string.connecting)
                }
            }
            ResourceState.SUCCESS -> {
                try {
                    mLiveVideoBroadcaster.connect(data!!.getUrl())
                } catch (e: ConnectException) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    stop()
                } finally {
                    if (mLiveVideoBroadcaster.isConnected()) {
                        // We successfully connected, start countdown to broadcast
                        start()
                    } else {
                        stop()
                    }
                }
            }
            ResourceState.ERROR -> {
                Toast.makeText(requireContext(), "Unable to retrieve connection credentials.", Toast.LENGTH_SHORT)
                    .show()
                stop()
            }
        }
    }

    private fun stop() = lifecycleScope.launch(Dispatchers.Main) {
        stream_position_timer.stopTimer()
        // Clear the "keep screen on" flag
        if (mLiveVideoBroadcaster.isConnected()) {
            mLiveVideoBroadcaster.stopBroadcasting()
        }
        requireActivity().onBackPressed()
    }

    private fun start() = lifecycleScope.launch(Dispatchers.Main) {
        status_text.visibility = View.GONE
        countDownFrom(COUNTDOWN_MILLISECONDS) {
            // Start broadcasting
            lifecycleScope.launch(Dispatchers.IO) {
                mLiveVideoBroadcaster.startBroadcasting()
            }

            stream_position_timer.apply {
                startTimer(stream_progress)
                text = getString(R.string.zero_time)
            }
            stream_progress.isIndeterminate = false
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
        super.onDestroy()
        mLiveVideoBroadcaster.release()
        exitFullscreen(requireActivity())
    }

    data class StreamRequest(
        val title: String,
        val message: String,
        val requestMoment: String,
        val requestTimeout: String,
        val livestreamId: String,
        val vlogId: String
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
