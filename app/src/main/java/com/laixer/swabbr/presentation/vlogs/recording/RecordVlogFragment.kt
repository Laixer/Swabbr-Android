package com.laixer.swabbr.presentation.vlogs.recording

import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.recording.RecordVideoWithPreviewFragment
import com.laixer.swabbr.presentation.types.VideoRecordingState
import com.laixer.swabbr.presentation.utils.todosortme.gone
import com.laixer.swabbr.presentation.utils.todosortme.visible
import com.laixer.swabbr.services.uploading.ReactionUploadWorker
import com.laixer.swabbr.services.uploading.VlogUploadWorker
import kotlinx.android.synthetic.main.fragment_record_video_minmax.*
import kotlinx.android.synthetic.main.fragment_record_vlog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Duration

/**
 *  Fragment for recording a vlog.
 */
class RecordVlogFragment : RecordVideoWithPreviewFragment() {

    /**
     *  Counter indicating how many retries we have had.
     */
    private var attempts = 0

    /**
     *  Flag indicating if we are already in the countdown state.
     */
    private var isCountingDown = false

    /**
     *  Timer used to count us down.
     */
    private var timer: CountDownTimer? = null

    /**
     *  Set durations at create time.
     */
    init {
        setMinMaxDuration(MINIMUM_RECORD_TIME, MAXIMUM_RECORD_TIME)
    }

    /**
     *  Inflate our layout with countdown text view.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_record_vlog, container, false)

    /**
     *  Start the countdown when we are ready to record.
     */
    override fun onStateChanged(state: VideoRecordingState) {
        // First call super to allow extended functionality to execute.
        super.onStateChanged(state)

        // Only start the countdown once.
        if (state == VideoRecordingState.READY && !isCountingDown) {
            isCountingDown = true
            startRecordingCountdown()
        }

        // Increase attempts by 1.
        if (state == VideoRecordingState.DONE_RECORDING) {
            attempts += 1
        }
    }

    /**
     *  Also reset the countdown flag.
     */
    override fun tryReset() {
        isCountingDown = false

        super.tryReset()
    }

    /**
     *  Post the reaction when we confirm, and go back to where
     *  we came from.
     */
    override fun onPreviewConfirmed() {
        super.onPreviewConfirmed()

        // TODO This doesn't seem to do anything currently
        // Broadcasts the media file to the rest of the system.
        // Note that does not broadcast to the media gallery,
        // which it should be doing.
        MediaScannerConnection.scanFile(
            requireContext(),
            arrayOf(outputFile.absolutePath),
            arrayOf(VIDEO_MIME_TYPE),
            null
        )

        // FUTURE: Implement isPrivate
        // Dispatch the uploading process
        VlogUploadWorker.enqueue(
            context = requireContext(),
            userId = getSelfId(),
            videoFile = outputFile,
            isPrivate = false
        )

        // Go to our own vlogs, removing this from the back stack
        findNavController().navigate(RecordVlogFragmentDirections.actionRecordVlogFragmentToProfileFragment("self"))
    }

    /**
     *  If we have attempts left we may try again, so reset. Else
     *  we will be navigated to our dashboard.
     */
    override fun onPreviewDeclined() {
        super.onPreviewDeclined()

        if (attempts >= MAXIMUM_RECORD_ATTEMPTS) {
            showMessage("Maximum vlog attempts exceeded")
            findNavController().navigate(RecordVlogFragmentDirections.actionGlobalDashboardFragment())
        } else {
            val left = MAXIMUM_RECORD_ATTEMPTS - attempts
            val word = if (left > 1) "attempts" else "attempt"
            showMessage("$left $word left")

            tryReset()
        }
    }

    /**
     *  Starts the recording process using the countdown UI.
     */
    private fun startRecordingCountdown() = lifecycleScope.launch(Dispatchers.Main) {
        text_view_record_vlog_countdown?.visible()

        // Cancel any existing timer
        if (timer != null) {
            timer?.cancel()
        }

        // Timer object handling our UI.
        timer = object : CountDownTimer(COUNTDOWN_MILLISECONDS, COUNTDOWN_INTERVAL_MILLISECONDS) {
            override fun onTick(millisUntilFinished: Long) {
                // Only proceed if we are ready or switching camera.
                if (getCurrentState() != VideoRecordingState.READY &&
                    getCurrentState() != VideoRecordingState.INITIALIZING_CAMERA
                ) {
                    // Hide and abort.
                    text_view_record_vlog_countdown?.gone()
                    this.cancel()
                    return
                }

                // Display the countdown on screen, updating each second.
                text_view_record_vlog_countdown?.text =
                    String.format("%1d", (millisUntilFinished / COUNTDOWN_INTERVAL_MILLISECONDS) + 1)

                // If we have only one second left, disable the camera switcher.
                if (millisUntilFinished <= 1100) {
                    button_switch_camera?.isEnabled = false
                }
            }

            // Hide the countdown text when we are finished, then start recording.
            override fun onFinish() {
                text_view_record_vlog_countdown?.gone()

                lifecycleScope.launch(Dispatchers.Main) {
                    if (getCurrentState() == VideoRecordingState.READY) {
                        tryStartRecording()
                    }
                }
            }
        }

        timer?.start()
    }

    /**
     *  Catch us exiting the countdown.
     */
    override fun onPause() {
        super.onPause()

        // If we are in the countdown, cancel the timer
        if (getCurrentState() == VideoRecordingState.READY && isCountingDown) {
            timer?.cancel()
            text_view_record_vlog_countdown.gone()
        }
    }

    /**
     *  Clean up resources.
     */
    override fun onDestroy() {
        super.onDestroy()

        timer = null
    }

    companion object {
        private val TAG = RecordVlogFragment::class.java.simpleName

        private const val COUNTDOWN_MILLISECONDS = 5_000L
        private const val COUNTDOWN_INTERVAL_MILLISECONDS = 1_000L
        private const val MAXIMUM_RECORD_ATTEMPTS = 2

        private val MINIMUM_RECORD_TIME = Duration.ofSeconds(10)
        private val MAXIMUM_RECORD_TIME = Duration.ofMinutes(10)
    }

}
