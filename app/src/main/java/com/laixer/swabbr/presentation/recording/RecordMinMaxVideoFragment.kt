package com.laixer.swabbr.presentation.recording

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.lifecycle.lifecycleScope
import com.laixer.presentation.gone
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.types.VideoRecordingState
import kotlinx.android.synthetic.main.fragment_record_video_minmax.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.util.*

// TODO Use view.post maybe? Or some other non-timer solution? Might be more elegant than all the run-on-UI calls...
// TODO Torch
/**
 *  Fragment for recording a video with a minimum and maximum
 *  recording time. Call [setMinMaxDuration] to apply these
 *  constraints. If this method isn't called, default values
 *  are applied.
 */
open class RecordMinMaxVideoFragment : RecordVideoFragment() {

    /**
     *  Minimum recorded video length.
     */
    private var minimumVideoDuration: Duration = defaultMinimumVideoDuration

    /**
     *  Maximum recorded video length.
     */
    private var maximumVideoDuration: Duration = defaultMaximumVideoDuration

    /**
     *  Stored recording start time. Re-set in [tryStartRecording].
     */
    private var recordingStartTime: Instant? = null

    /**
     *  Timer which is used to trigger
     */
    private var timer: Timer? = null

    /**
     *  Inflate our view.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_record_video_minmax, container, false)

    /**
     *  Explicitly change some UI elements right away.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_start_stop_recording.setMinMaxDuration(minimumVideoDuration, maximumVideoDuration)

        button_switch_camera?.setOnClickListener { trySwitchCamera() }
        button_switch_camera?.isEnabled = false

        // TODO Torch
        button_torch?.gone()
    }

    /**
     *  Sets the minimum and maximum duration of this video.
     */
    protected fun setMinMaxDuration(minimumVideoDuration: Duration, maximumVideoDuration: Duration) {
        if (minimumVideoDuration >= maximumVideoDuration) {
            throw IllegalArgumentException("Minimum duration can't be longer than maximum duration")
        }

        this.minimumVideoDuration = minimumVideoDuration
        this.maximumVideoDuration = maximumVideoDuration
    }

    /**
     *  When we enter [VideoRecordingState.RECORDING] this will
     *  start a timer enforcing the video length constraints.
     *  This also handles desired UI updates.
     */
    @CallSuper
    override fun onStateChanged(state: VideoRecordingState) {
        super.onStateChanged(state)

        // UI updates
        lifecycleScope.launch(Dispatchers.Main) {
            when (state) {
                VideoRecordingState.READY -> {
                    button_start_stop_recording?.setCustomOnClickListener { tryStartRecording() }
                    button_start_stop_recording?.setReady()

                    button_switch_camera?.isEnabled = true
                }
                VideoRecordingState.RECORDING -> {
                    button_start_stop_recording?.setCustomOnClickListener { tryStopRecording() }

                    button_switch_camera?.isEnabled = false
                }
                VideoRecordingState.DONE_RECORDING -> {
                    // TODO This is dev
                    // Launch external activity via intent to play video recorded using our provider
//                    startActivity(Intent().apply {
//                        action = Intent.ACTION_VIEW
//                        type = MimeTypeMap.getSingleton()
//                            .getMimeTypeFromExtension(outputFile.extension)
//                        val authority = "${BuildConfig.APPLICATION_ID}.provider"
//                        data = FileProvider.getUriForFile(requireContext(), authority, outputFile)
//                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
//                            Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    })
                }
                else -> {
                    button_start_stop_recording?.setCustomOnClickListener { tryStartRecording() }
                }
            }
        }

        // Timer management (separate thread).
        when (state) {
            VideoRecordingState.RECORDING -> {
                // Cancel any existing timers. // TODO This should never happen.
                cancelTimer()

                // Set the start time.
                recordingStartTime = Instant.now()

                // Schedule our min and max callbacks.
                timer = Timer(TIMER_NAME).apply {
                    this.schedule(object : TimerTask() {
                        override fun run() {
                            onMinimumRecordingTimeElapsed()
                        }
                    }, minimumVideoDuration.toMillis())

                    this.schedule(object : TimerTask() {
                        override fun run() {
                            onMaximumRecordingTimeElapsed()
                        }
                    }, maximumVideoDuration.toMillis())
                }
            }
            else -> {
                cancelTimer()
            }
        }
    }

    /**
     *  Stops the [timer] object and discards it.
     */
    private fun cancelTimer() {
        timer?.cancel()
        timer = null
    }

    /**
     *  Checks if our minimum recording time has elapsed or not.
     *  If [recordingStartTime] is not assigned this returns false.
     */
    private fun hasMinimumRecordingTimeElapsed(): Boolean =
        recordingStartTime?.let {
            Instant.now().toEpochMilli() >= it.plus(minimumVideoDuration).toEpochMilli()
        } ?: false

    /**
     *  Called [minimumVideoDuration] after we start recording.
     *  This will enable the [button_start_stop_recording].
     */
    @CallSuper
    protected open fun onMinimumRecordingTimeElapsed() {
        lifecycleScope.launch(Dispatchers.Main) {
            button_start_stop_recording?.onMinimumRecordTimeElapsed()
        }
    }

    /**
     *  Called [maximumVideoDuration] after we start recording.
     *  This will attempt to stop recording.
     */
    @CallSuper
    protected open fun onMaximumRecordingTimeElapsed() {
        lifecycleScope.launch(Dispatchers.Main) {
            tryStopRecording()
        }
    }

    /**
     *  Notifies our [button_start_stop_recording].
     */
    override fun tryStartRecording() {
        super.tryStartRecording()
        button_start_stop_recording?.onStartRecording()
    }

    /**
     *  Only tries to stops recording if our [minimumVideoDuration] has elapsed.
     */
    override fun tryStopRecording() {
        if (hasMinimumRecordingTimeElapsed()) {
            button_start_stop_recording?.setDisabled()
            super.tryStopRecording()
        }
    }

    companion object {
        /**
         *  Default for [minimumVideoDuration].
         */
        val defaultMinimumVideoDuration: Duration = Duration.ofSeconds(1)

        /**
         *  Default for [maximumVideoDuration].
         */
        val defaultMaximumVideoDuration: Duration = Duration.ofSeconds(300)

        private val TAG = this::class.java.simpleName
        private val TIMER_NAME = "$TAG timer"
    }
}
