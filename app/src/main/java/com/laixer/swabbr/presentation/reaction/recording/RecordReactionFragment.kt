package com.laixer.swabbr.presentation.reaction.recording

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.laixer.swabbr.BuildConfig
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.reaction.playback.ReactionViewModel
import com.laixer.swabbr.presentation.recording.RecordVideoFragment2
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.video_confirm_dialogue.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Duration
import java.util.*

/**
 *  Fragment for recording a reaction. The reaction is buffered into a file,
 *  and a playback & confirmation popup is shown after recording. When the
 *  user decides to proceed with the reaction, the file is uploaded to the
 *  blob storage and the backend is notified of this.
 *
 *  Note that the [args] cameraId is not used but is hardcoded in the
 *  [RecordVideoFragment2]. See the to do in that file.
 */
class RecordReactionFragment : RecordVideoFragment2() {
    /** AndroidX navigation arguments */
    private val args: RecordReactionFragmentArgs by navArgs()
    private val reactionVm: ReactionViewModel by viewModel()

    private val targetVlogId: UUID by lazy { UUID.fromString(args.vlogId) }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enforce the vlog constraints.
        initMinMaxVideoTimes(MINIMUM_RECORD_TIME, MAXIMUM_RECORD_TIME)
    }

    /**
     *  Stops the recording process and creates a popup in which the user
     *  can either conform or cancel the reaction operation. If the user
     *  confirms, the reaction is uploaded and posted to the backend.
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

                preview_cancel.setOnClickListener {
                    reset()
                    dismiss()
                }

                preview_ok.setOnClickListener {
                    dismiss()
                    upload(localVideoUri, localThumbnailUri)

                    // Go back right away.
                    findNavController().popBackStack()
                }
            }.show()
        }
    }

    /**
     *  Called when the user confirms the reaction post after playback.
     *  This method includes success and failure callbacks.
     *
     *  @param localVideoUri Locally stored video file uri.
     *  @param localThumbnailUri Locally stored thumbnail file uri.
     */
    private fun upload(localVideoUri: Uri, localThumbnailUri: Uri) {
        lifecycleScope.launch(Dispatchers.Main) {
            reactionVm.postReaction(localVideoUri, localThumbnailUri, targetVlogId, false)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "The reaction has been posted!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Failed to upload reaction, please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
        }
    }

    companion object {
        private val TAG = this::class.java.simpleName

        private val MINIMUM_RECORD_TIME = Duration.ofSeconds(3)
        private val MAXIMUM_RECORD_TIME = Duration.ofSeconds(10)
    }

}
