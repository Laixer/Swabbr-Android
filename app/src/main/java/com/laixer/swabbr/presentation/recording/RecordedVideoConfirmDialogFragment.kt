package com.laixer.swabbr.presentation.recording

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.DialogFragment
import com.laixer.swabbr.R
import kotlinx.android.synthetic.main.fragment_recorded_video_confirm.*

/**
 *  Fragment displaying a video from a URI along with
 *  post / cancel buttons. This is based on the example at
 *  https://github.com/google-developer-training/android-advanced/blob/master/SimpleVideoView/app/src/main/java/com/example/android/simplevideoview/MainActivity.java
 *
 *  // TODO Dichttimmeren!
 *  @param videoFileUri The uri of the video file.
 *  @param onConfirmCallback What to do when we confirm.
 *  @param onCancelCallback What to do when we cancel.
 */
class RecordedVideoConfirmDialogFragment(
    private val videoFileUri: Uri,
    private val onConfirmCallback: () -> Unit,
    private val onCancelCallback: () -> Unit
) : DialogFragment() {

    /**
     *  Controls the media we display.
     */
    private lateinit var mediaController: MediaController

    /**
     *  Current playback position in millis, used in saved instance.
     */
    private var currentPosition: Int? = null

    /**
     *  Inflate our view.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_recorded_video_confirm, container, false)

    /**
     *  Restore the current position if present in [savedInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prevent the back button from being effective.
        isCancelable = false

        // TODO
        // Set to full screen
        // setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(KEY_PLAYBACK_TIME);
        }
    }

    /**
     *  Bind our UI.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_recorded_video_post.setOnClickListener { onConfirmCallback.invoke() }
        button_recorded_video_cancel.setOnClickListener { onCancelCallback.invoke() }

        mediaController = MediaController(requireContext())
        mediaController.setMediaPlayer(video_view_recorded_video)
        video_view_recorded_video.setMediaController(mediaController)
    }

    /**
     *  Save the current playback position to the instance state bundle.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_PLAYBACK_TIME, video_view_recorded_video.getCurrentPosition())
    }

    /**
     *  Load the media each time our fragment starts.
     */
    override fun onStart() {
        super.onStart()

        initializeVideo()
    }

    /**
     *  Pause the video if we leave the fragment.
     */
    override fun onPause() {
        super.onPause()

        video_view_recorded_video.pause()
    }

    /**
     *  Release any claimed resources.
     */
    override fun onStop() {
        super.onStop()

        releasePlayer()
    }

    /**
     *  Loads and displays our video.
     */
    private fun initializeVideo() {
        // Buffer and decode the video sample.
        video_view_recorded_video.setVideoURI(videoFileUri)

        // Start playing when the media is ready.
        video_view_recorded_video.setOnPreparedListener {
            video_view_recorded_video.seekTo(currentPosition ?: 1) // 1 shows the first frame of the video
            video_view_recorded_video.start()
        }

        // Return the video position to the start when playback completes.
        video_view_recorded_video.setOnCompletionListener {
            video_view_recorded_video.seekTo(0) // 0 shows no frame but sets us at the start
        }
    }

    /**
     *  Release resources.
     */
    private fun releasePlayer() {
        video_view_recorded_video.stopPlayback()
    }

    companion object {
        private const val KEY_PLAYBACK_TIME = "Playback Time"
    }
}
