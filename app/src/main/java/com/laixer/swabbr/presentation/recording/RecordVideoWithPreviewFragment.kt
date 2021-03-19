package com.laixer.swabbr.presentation.recording

import androidx.annotation.CallSuper
import androidx.core.net.toUri
import com.laixer.swabbr.presentation.types.VideoRecordingState

// TODO Manage back presses, also with dialog cancel
/**
 *  Wrapper around [RecordMinMaxVideoFragment] with a preview dialog.
 *  Once again the splitting of functionality is intentional, see
 *  the docs of the classes this inherits from for more explanation.
 */
open class RecordVideoWithPreviewFragment : RecordMinMaxVideoFragment() {

    // TODO Additional styling for buttons etc?
    /**
     *  Popup object for our confirmation fragment.
     */
    private var popup: RecordedVideoConfirmDialogFragment? = null

    /**
     *  Create the popup.
     */
    override fun onStateChanged(state: VideoRecordingState) {
        super.onStateChanged(state)

        if (state == VideoRecordingState.DONE_RECORDING) {
            popup = RecordedVideoConfirmDialogFragment(outputFile.toUri(), ::onPreviewConfirmed, ::onPreviewDeclined)
            popup?.showNow(childFragmentManager, TAG)
        }
    }

    // TODO Docs https://developer.android.com/reference/android/app/DialogFragment.html
    // TODO Should this dismissing be done by popup dialog itself? Think so (maybe with a boolean flag to bypass) ...
    /**
     *  Override this to determine functionality for confirming
     *  the dialog, other than dismissing the dialog.
     */
    @CallSuper
    protected open fun onPreviewConfirmed() {
        popup?.dismiss()

        // TODO Remove debug
        tryReset()
    }

    /**
     *  Override this to determine functionality for cancelling
     *  the dialog, other than dismissing the dialog.
     */
    @CallSuper
    protected open fun onPreviewDeclined() {
        popup?.dismiss()
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}
