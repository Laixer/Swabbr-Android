package com.laixer.swabbr.presentation.vlogs.recording

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.laixer.swabbr.BuildConfig
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.MainActivity
import com.laixer.swabbr.presentation.recording.RecordVideoFragment
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
 */
class RecordVlogFragment : RecordVideoFragment() {
    private val vlogVm: VlogRecordingViewModel by viewModel()

    /**
     *  This sets up the UI in order to start the countdown and
     *  control the recording button accordingly.
     */
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enforce the vlog constraints.
        // initMinMaxVideoTimes(MINIMUM_RECORD_TIME, MAXIMUM_RECORD_TIME)

        // Trigger the recording countdown and start vlogging.
        // startRecordingCountdown()
    }

    /**
     *  Stops the recording process and creates a popup in which the user
     *  can either conform or cancel the vlog operation. If the user
     *  confirms, the vlog is uploaded and posted to the backend.
     */
    override fun stopRecording() {
        super.stopRecording()

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
    private fun
        upload(localVideoUri: Uri, localThumbnailUri: Uri) {
        lifecycleScope.launch(Dispatchers.Main) {
            vlogVm.postVlog(localVideoUri, localThumbnailUri, false)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        lifecycleScope.launch(Dispatchers.Main) {
                            showMessage("The vlog has been posted!")
                            requireActivity().onBackPressed()
                        }
                    },
                    {
                        lifecycleScope.launch(Dispatchers.Main) {
                            showMessage("Failed to upload vlog, please try again.")
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
