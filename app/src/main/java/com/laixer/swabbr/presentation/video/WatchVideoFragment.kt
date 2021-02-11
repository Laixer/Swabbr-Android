package com.laixer.swabbr.presentation.video

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.AuthFragment
import kotlinx.android.synthetic.main.fragment_video.*

// TODO Orientation is wrong sometimes.
// TODO Swipe refresh layout?
/**
 *  Fragment for video playback. This is used both for vlog
 *  playback and for reaction playback. This implements the
 *  [Player.EventListener] interface to allow us to observe
 *  [video_player] events.
 */
open class WatchVideoFragment : Player.EventListener, AuthFragment() {
    private val exoPlayer: ExoPlayer by lazy { ExoPlayerFactory.newSimpleInstance(requireContext()) }

    /**
     *  Inflates our layout.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_video, container, false)
    }

    /**
     *  Sets the loading icon to visible. This is disabled again in [stream].
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add this object as an event listener to the player.
        exoPlayer.addListener(this)
    }

    /**
     *  Attempts to stream video from a given https endpoint. Note that
     *  no tokens or encryption is expected, the endpoint should be pre-
     *  signed. This also hides the loading icon.
     *
     *  @param endpoint Resource location.
     */
    protected fun stream(endpoint: Uri) {
        // TODO video_playback_loading_icon?.visibility = View.GONE

        val dataSourceFactory = DefaultHttpDataSourceFactory(requireContext().getString(R.string.app_name))

        val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
        val mediaSource = mediaSourceFactory.createMediaSource(endpoint)

        video_content_loading_icon.visibility = View.GONE
        exoPlayer.apply {
            prepare(mediaSource, true, false)
            setForegroundMode(false)
        }

        video_player.apply {
            controllerShowTimeoutMs = -1
            controllerHideOnTouch = false
            player = exoPlayer
        }
    }

    // TODO This is fragment.onResume(). Correct? I think not.
    override fun onResume() {
        super.onResume()
        exoPlayer.playWhenReady = true
        exoPlayer.seekTo(0)
    }

    // TODO This is fragment.onPause(). Correct? I Think not.
    override fun onPause() {
        super.onPause()
        video_player?.overlayFrameLayout?.removeAllViews()
        exoPlayer.seekTo(0)
        exoPlayer.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}
