package com.laixer.swabbr.presentation.reaction.recording

import android.media.MediaScannerConnection
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.laixer.swabbr.extensions.goBack
import com.laixer.swabbr.presentation.recording.RecordVideoWithPreviewFragment
import com.laixer.swabbr.presentation.types.VideoRecordingState
import com.laixer.swabbr.services.uploading.ReactionUploadWorker
import kotlinx.android.synthetic.main.fragment_record_video_minmax.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Duration
import java.util.*

/**
 *  Fragment for recording a reaction.
 */
class RecordReactionFragment : RecordVideoWithPreviewFragment() {

    private val args: RecordReactionFragmentArgs by navArgs()
    private val targetVlogId: UUID by lazy { UUID.fromString(args.targetVlogId) }

    /**
     *  Enforce video constraints.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setMinMaxDuration(MINIMUM_RECORD_TIME, MAXIMUM_RECORD_TIME)
    }

    /**
     *  When we reach the [VideoRecordingState.READY] state, enable the recording button.
     */
    override fun onStateChanged(state: VideoRecordingState) {
        super.onStateChanged(state)

        if (state == VideoRecordingState.READY) {
            lifecycleScope.launch(Dispatchers.Main) {
                button_start_stop_recording?.setEnabled()
            }
        }
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
        ReactionUploadWorker.enqueue(
            context = requireContext(),
            userId = getSelfId(),
            videoFile = outputFile,
            targetVlogId = targetVlogId,
            isPrivate = false
        )

        // Go back, which should take us back to the vlog we are posting to.
        goBack()
    }

    /**
     *  Simply reset so we can begin recording again.
     *  Infinite retries are allowed
     */
    override fun onPreviewDeclined() {
        super.onPreviewDeclined()

        tryReset()
    }

    companion object {
        private val TAG = RecordReactionFragment::class.java.simpleName

        private val MINIMUM_RECORD_TIME = Duration.ofSeconds(3)
        private val MAXIMUM_RECORD_TIME = Duration.ofSeconds(10)
    }

}
