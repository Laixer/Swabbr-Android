package com.laixer.swabbr.presentation.vlogdetails

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.laixer.swabbr.presentation.model.ProfileVlogItem
import com.laixer.swabbr.presentation.model.getUrlString

class VlogFragment : Fragment() {

    private var videoView: VideoView? = null
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var profileVlogItem: ProfileVlogItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileVlogItem = arguments!!.getSerializable(PROFILEVLOGITEM_KEY) as ProfileVlogItem

        val dataSourceFactory: DataSource.Factory = when (profileVlogItem.isLive) {
            true -> DefaultHttpDataSourceFactory(Util.getUserAgent(this.context, "Swabbr"))
            false -> DefaultDataSourceFactory(this.context, Util.getUserAgent(this.context, "Swabbr"))
        }

        // Check if we need to create a progressive or HLS datasource
        val mediaSourceFactory = when (profileVlogItem.isLive) {
            true -> HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true)
            false -> ProgressiveMediaSource.Factory(dataSourceFactory)
        }

        val uri = Uri.parse(profileVlogItem.getUrlString())
        val mediaSource = mediaSourceFactory.createMediaSource(uri)

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this.context)
        exoPlayer.prepare(mediaSource, true, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        videoView = VideoView(inflater, container)
        return videoView?.view
    }

    override fun onResume() {
        super.onResume()
        videoView?.bind(exoPlayer, profileVlogItem, requireContext())
        exoPlayer.playWhenReady = true
        if (!profileVlogItem.isLive) {
            exoPlayer.seekTo(0)
        }
    }

    override fun onPause() {
        super.onPause()
        videoView?.unbind()
        if (!profileVlogItem.isLive) {
            exoPlayer.seekTo(0)
        }
        exoPlayer.playWhenReady = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        videoView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

    companion object {
        private val PROFILEVLOGITEM_KEY = "PROFILEVLOGITEM"

        fun create(item: ProfileVlogItem): VlogFragment {
            val fragment = VlogFragment()
            val bundle = Bundle()
            bundle.putSerializable(PROFILEVLOGITEM_KEY, item)
            fragment.arguments = bundle
            return fragment
        }
    }
}
