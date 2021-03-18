package com.laixer.swabbr.presentation.video

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.laixer.swabbr.presentation.utils.todosortme.gone
import com.laixer.swabbr.presentation.utils.todosortme.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.auth.AuthFragment
import com.laixer.swabbr.presentation.types.VideoPlaybackState
import kotlinx.android.synthetic.main.fragment_video.*

// TODO Orientation is wrong sometimes.
// TODO Swipe refresh layout?
// TODO When exiting and re-entering this - save the timestamp and continue there.
// TODO Make autoplay a toggle
/**
 *  Fragment for video playback. This is used both for vlog
 *  playback and for reaction playback. This implements the
 *  [Player.EventListener] interface to allow us to observe
 *  [video_player] events.
 */
abstract class WatchVideoFragment : Player.EventListener, AuthFragment() {
    // TODO Fully implement this for all states. https://github.com/Laixer/Swabbr-Android/issues/202
    /**
     *  Observe this to be notified when the video playback state changes.
     */
    val videoStateLiveData: MutableLiveData<VideoPlaybackState> = MutableLiveData()

    /**
     *  Actual player, which will be disposed and re-created
     *  whenever we exit / enter this fragment.
     */
    private var exoPlayer: ExoPlayer? = null

    /**
     *  Contains our video data source for playback.
     */
    private var mediaSource: MediaSource? = null

    /**
     *  Inflates our layout.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_video, container, false)
    }

    /**
     *  Sets the loading icon to visible. This is disabled again in [loadMediaSource].
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** Always show the loading icon, which will be disabled in [loadMediaSource]. */
        video_content_loading_icon.visible()

        // Always hide the error message.
        text_display_video_playback_error.gone()

        // Fit to screen.
        video_player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
    }

    // TODO STATE_ENDED enters twice in the profile vlogs viewpager -> reaction playback. Why?
    /**
     *  Manages [videoStateLiveData] based on the player state.
     */
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> videoStateLiveData.value = VideoPlaybackState.READY
            Player.STATE_ENDED -> videoStateLiveData.value = VideoPlaybackState.FINISHED

            // TODO Other states https://github.com/Laixer/Swabbr-Android/issues/202
            // Player.STATE_IDLE -> videoStateLiveData.value = VideoPlaybackState.READY
            // Player.STATE_BUFFERING -> videoStateLiveData.value = VideoPlaybackState.READY
        }
    }

    // TODO Call this at creation to decrease loading time.
    /**
     *  This loads our video from a given [endpoint] through a signed uri
     *  and should only be called once per fragment.
     *
     *  This calls [playMediaSourceIfPresent] when the media has been loaded.
     *
     *  @param endpoint Resource location.
     */
    protected fun loadMediaSource(endpoint: Uri) {
        // Throw if we already have a media source.
        if (mediaSource != null) {
            throw IllegalStateException("Can't overwrite media source")
        }

        val dataSourceFactory = DefaultHttpDataSourceFactory(requireContext().getString(R.string.app_name))
        val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
        mediaSource = mediaSourceFactory.createMediaSource(endpoint)

        playMediaSourceIfPresent()
    }

    /**
     *  Call this when we can't load the video resource.
     *
     *  @param stringResource Display message.
     */
    protected fun onResourceError(stringResource: Int = R.string.error_load_video) = onError(stringResource)

    /**
     *  Removes any displayed error messages if present.
     */
    protected fun clearErrorIfPresent() {
        text_display_video_playback_error.gone()

        video_player.showController()
    }

    /**
     *  Display the error message and release the player.
     *
     *  @param stringResource Display text for [text_display_video_playback_error].
     */
    private fun onError(stringResource: Int = R.string.error_playback_video) {
        releasePlayer()

        text_display_video_playback_error.visible()
        text_display_video_playback_error.text = requireContext().getString(stringResource)

        video_player.hideController()
    }

    /**
     *  Sets the [exoPlayer] up for playback.
     */
    private fun initializePlayer() {
        // Only perform player modifications if it's null.
        if (exoPlayer == null) {
            exoPlayer = ExoPlayerFactory.newSimpleInstance(requireContext())

            val listener = this // TODO Ugly syntax.
            exoPlayer?.apply {
                /** Add this object as an event listener to the player.
                 *  See the [Player.EventListener] interface for more info. */
                addListener(listener)

                // Enable autoplay.
                playWhenReady = true

                // Don't make the player hard-claim resources.
                setForegroundMode(false)
            }

            // Setup the UI player element.
            video_player.apply {
                // Always show the video controller bar UI
                controllerShowTimeoutMs = -1
                controllerHideOnTouch = false

                player = exoPlayer
            }
        }
    }

    /**
     *  If we have both an [exoPlayer] and a [mediaSource], play it.
     *  This also removes the loading icon.
     */
    private fun playMediaSourceIfPresent() {
        if (mediaSource != null && exoPlayer != null) {
            exoPlayer!!.prepare(mediaSource, true, false)

            video_content_loading_icon.gone()
        }
    }

    /**
     *  Releases the player, as required and recommended in the docs.
     *  This should be called whenever we leave the current fragment,
     *  whether that is through swiping, a temporary popup fragment or
     *  permanently.
     *
     *  Note that after releasing the exoplayer the [video_player] still
     *  displays the last displayed frame.
     */
    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    /**
     *  Called when the exo player throws an exception.
     */
    override fun onPlayerError(@NonNull e: ExoPlaybackException?) {
        super.onPlayerError(e)
        onError(R.string.error_playback_video)
    }

    /**
     *  This method guarantees that we have an instantiated
     *  view and is called after said instantiation.
     */
    override fun onStart() {
        super.onStart()
    }

    /**
     *  Called when the fragment is ready for user interaction.
     *  This will also be called when a swiping animation has
     *  finished. This will attempt to start video playback.
     */
    override fun onResume() {
        super.onResume()

        initializePlayer()
        playMediaSourceIfPresent()
    }

    /**
     *  Called when the user begins to leave the fragment while
     *  the fragment itself is still visible. This releases the
     *  exoplayer. Note that this also gets called when we are
     *  leaving the fragment completely, hence [releasePlayer]
     *  isn't called in [onStop].
     */
    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    /**
     *  Dispose view resources, called when the fragments
     *  view has been detached from the window.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer = null
        mediaSource = null
    }
}
