package com.laixer.swabbr.presentation.vlogdetails

import android.net.Uri
import android.os.Bundle
import android.transition.ChangeBounds
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.UserVlogItem
import kotlinx.android.synthetic.main.include_user_info_reversed.view.*
import kotlinx.android.synthetic.main.item_vlog.view.*

class VlogFragment : Fragment() {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var userVlogItem: UserVlogItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userVlogItem = requireArguments().getSerializable(PROFILEVLOGITEM_KEY) as UserVlogItem
        val dataSourceFactory: DataSource.Factory =
            DefaultHttpDataSourceFactory(Util.getUserAgent(this.context, "Swabbr"))

        val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
        val uri = Uri.parse(userVlogItem.url.toURI().toString())
        val mediaSource = mediaSourceFactory.createMediaSource(uri)

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this.context)
        exoPlayer.prepare(mediaSource, true, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sharedElementEnterTransition = ChangeBounds().apply {
            duration = 750
        }
        sharedElementReturnTransition = ChangeBounds().apply {
            duration = 750
        }
        val view = layoutInflater.inflate(R.layout.item_vlog, container, false)
        val overlayView = layoutInflater.inflate(R.layout.video_view_overlay, container, false)
        overlayView.reversed_userAvatar.loadAvatar(userVlogItem.profileImage,  userVlogItem.userId)
        overlayView.reversed_userUsername.text = requireContext().getString(R.string.nickname, userVlogItem.nickname)
        overlayView.reversed_userName.text = requireContext().getString(
            R.string.full_name, userVlogItem.firstName, userVlogItem
                .lastName
        )

        val exo = view.player
        exo.overlayFrameLayout?.addView(overlayView)
        exoPlayer.setForegroundMode(false)
        exo.player = exoPlayer

        exo.showController()

        return view
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

    companion object {
        private val PROFILEVLOGITEM_KEY = "PROFILEVLOGITEM"

        fun create(item: UserVlogItem): VlogFragment {
            val fragment = VlogFragment()
            val bundle = Bundle()
            bundle.putSerializable(PROFILEVLOGITEM_KEY, item)
            fragment.arguments = bundle
            return fragment
        }
    }
}
