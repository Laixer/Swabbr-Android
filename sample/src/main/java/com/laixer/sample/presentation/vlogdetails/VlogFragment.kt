package com.laixer.sample.presentation.vlogdetails

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.laixer.sample.presentation.model.ProfileVlogItem
import com.laixer.sample.presentation.model.getUrlString

class VlogFragment : Fragment() {

    private var videoView: VideoView? = null
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var dataSourceFactory: DefaultDataSourceFactory
    private lateinit var videoSourceFactory: ProgressiveMediaSource.Factory
    private lateinit var uri: Uri
    private lateinit var videoSource: ProgressiveMediaSource
    private lateinit var profileVlogItem: ProfileVlogItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileVlogItem = arguments!!.getSerializable(PROFILEVLOGITEM_KEY) as ProfileVlogItem
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this.context)
        dataSourceFactory = DefaultDataSourceFactory(this.context, Util.getUserAgent(this.context, "Swabbr"))
        videoSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
        uri = Uri.parse(profileVlogItem.getUrlString())
        videoSource = videoSourceFactory.createMediaSource(uri)
        exoPlayer.prepare(videoSource, true, false)
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
        exoPlayer.seekTo(0)
    }

    override fun onPause() {
        super.onPause()
        videoView?.unbind()
        exoPlayer.seekTo(0)
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
