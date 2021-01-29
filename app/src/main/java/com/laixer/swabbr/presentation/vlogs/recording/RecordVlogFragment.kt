package com.laixer.swabbr.presentation.vlogs.recording

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.laixer.swabbr.BuildConfig
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.MainActivity
import com.laixer.swabbr.presentation.recording.RecordVideoFragment
import com.laixer.swabbr.utils.lastMinuteSeconds
import com.laixer.swabbr.utils.minutes
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_record_video.*
import kotlinx.android.synthetic.main.video_confirm_dialogue.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Duration

// TODO Implement the request timeout in here as well.
/**
 *  Fragment for recording a vlog. The vlog is buffered into a file, and
 *  a playback & confirmation popup is shown after recording. When the
 *  user decides to proceed with the vlog , the file is uploaded to the
 *  blob storage and the backend is notified of this.
 *
 *  Note that the [args] cameraId is not used but is hardcoded in the
 *  [RecordVideoFragment]. See the to do in that file.
 */
class RecordVlogFragment : RecordVideoFragment() {
    /** AndroidX navigation arguments */
    private val args: RecordVlogActivityArgs by navArgs() // TODO Should be fragment args
    private val vlogVm: VlogRecordingViewModel by viewModel()

    /**
     *  This sets up the UI in order to start the countdown and
     *  control the recording button accordingly.
     */
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enforce the vlog constraints.
        initMinMaxVideoTimes(MINIMUM_RECORD_TIME, MAXIMUM_RECORD_TIME)

        // Disable the recording button and assign the stop() function
        // as click listener. This button will be enabled again after
        // the minimum vlog time has exceeded. It can the be used to
        // stop recording.
        capture_button.apply {
            isEnabled = false
            setOnClickListener { stop() }
        }

        // Setup the "one minute left" trigger popup
        stream_position_timer.apply {
            // TODO In the case of vlogging recording time < 1 minute, this will act weird.
            // TODO Dangerous cast to int
            addEventAt(
                MAXIMUM_RECORD_TIME.minutes().toInt() - 1,
                MAXIMUM_RECORD_TIME.lastMinuteSeconds().toInt()
            ) {
                Toast.makeText(requireContext(), "One minute left!", Toast.LENGTH_LONG).show()
                setTextColor(Color.RED)
            }
        }

        // Trigger the recording countdown and start vlogging.
        startRecordingCountdown()
    }

    /**
     *  Starts the recording process using [countDownFrom].
     */
    private fun startRecordingCountdown() = lifecycleScope.launch(Dispatchers.Main) {
        // Specify what to do when the countdown exceeds.
        countDownFrom(COUNTDOWN_MILLISECONDS) {
            // Trigger the recording functionality and actually start the recording.
            super.start()
        }
    }

    /**
     *  Starts the countdown popup and performs some function when
     *  the time is up. Each second the UI will be updated.
     *
     *  @param countdownMs How long to count down in ms.
     *  @param onFinish What to do when the time is up.
     */
    private fun countDownFrom(countdownMs: Long, onFinish: suspend () -> Unit) {
        countdown.visibility = View.VISIBLE
        val timer = object : CountDownTimer(countdownMs, COUNTDOWN_INTERVAL_MILLISECONDS) {
            // Display the countdown on screen, updating each second.
            override fun onTick(millisUntilFinished: Long) {
                countdown.text = String.format("%1d", (millisUntilFinished / COUNTDOWN_INTERVAL_MILLISECONDS) + 1)
            }

            // Hide the countdown text when we are finished.
            override fun onFinish() {
                countdown.visibility = View.INVISIBLE
                lifecycleScope.launch(Dispatchers.Main) { onFinish() }
            }
        }
        timer.start()
    }

    /**
     *  Stops the recording process and creates a popup in which the user
     *  can either conform or cancel the vlog operation. If the user
     *  confirms, the vlog is uploaded and posted to the backend.
     */
    override fun stop() {
        super.stop()

        val authority = "${BuildConfig.APPLICATION_ID}.provider"
        val localVideoUri = FileProvider.getUriForFile(requireContext(), authority, videoFile)
        val localThumbnailUri = FileProvider.getUriForFile(requireContext(), authority, thumbnailFile)

        // TODO I believe this is a race condition. This should only run after stop() has completed.
        lifecycleScope.launch(Dispatchers.Main) {
            // Confirmation and playback popup
            Dialog(requireActivity()).apply {
                setCancelable(true)
                setContentView(R.layout.video_confirm_dialogue)

                preview_container.setVideoURI(localVideoUri)
                preview_container.start()

                // Pressing cancel goes to home
                preview_cancel.setOnClickListener {
                    val intent = Intent(this.context, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }

                // Pressing ok posts the vlog
                preview_ok.setOnClickListener {
                    dismiss()
                    upload(localVideoUri, localThumbnailUri)
                }
            }.show()
        }
    }

    // TODO Duplicate functionality with RecordReactionFragment
    /**
     *  Called when the user confirms the vlog post after playback.
     *  This method includes success and failure callbacks.
     *
     *  @param localVideoUri Locally stored video file uri.
     *  @param localThumbnailUri Locally stored thumbnail file uri.
     */
    private fun upload(localVideoUri: Uri, localThumbnailUri: Uri) {
        lifecycleScope.launch(Dispatchers.Main) {
            vlogVm.postVlog(localVideoUri, localThumbnailUri, false)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "The vlog has been posted!",
                                Toast.LENGTH_SHORT
                            ).show()
                            requireActivity().onBackPressed()
                        }
                    },
                    {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Failed to upload vlog, please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
        }
    }

    companion object {
        private val TAG = RecordVlogFragment::class.java.simpleName

        const val COUNTDOWN_MILLISECONDS = 5_000L
        const val COUNTDOWN_INTERVAL_MILLISECONDS = 1_000L

        private val MINIMUM_RECORD_TIME = Duration.ofSeconds(10)
        private val MAXIMUM_RECORD_TIME = Duration.ofMinutes(10)
    }

}
