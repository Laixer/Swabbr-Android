package com.laixer.swabbr.presentation.vlogs.details

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.AuthFragment
import kotlinx.android.synthetic.main.item_vlog.*
import kotlinx.android.synthetic.main.item_vlog.view.*

/**
 *  Fragment for video playback.
 */
open class VideoFragment : AuthFragment() {
    private val exoPlayer: ExoPlayer by lazy { ExoPlayerFactory.newSimpleInstance(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.item_vlog, container, false)
    }

    /**
     *  Attempts to stream video from a given https endpoint. Note that
     *  no tokens or encryption is expected, the endpoint should be pre-
     *  signed.
     *
     *  @param endpoint Resource location.
     */
    protected fun stream(endpoint: String) {
        val dataSourceFactory = DefaultHttpDataSourceFactory(requireContext().getString(R.string.app_name))

        // TODO Correct protocol!
        val mediaSourceFactory = HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true)
        val mediaSource = mediaSourceFactory.createMediaSource(Uri.parse(endpoint))

        content_loading_progressbar.visibility = View.GONE
        exoPlayer.apply {
            prepare(mediaSource, true, false)
            setForegroundMode(false)
        }

        player.apply {
            controllerShowTimeoutMs = -1
            controllerHideOnTouch = false
            player = exoPlayer
        }
    }

    override fun onResume() {
        super.onResume()
        exoPlayer.playWhenReady = true
        exoPlayer.seekTo(0)
    }

    override fun onPause() {
        super.onPause()
        view?.player?.overlayFrameLayout?.removeAllViews()
        exoPlayer.seekTo(0)
        exoPlayer.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}
