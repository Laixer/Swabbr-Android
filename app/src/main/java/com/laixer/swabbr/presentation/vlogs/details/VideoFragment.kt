package com.laixer.swabbr.presentation.vlogs.details

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.data.datasource.model.WatchVlogResponse
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.streaming.StreamViewModel
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.*
import com.plattysoft.leonids.ParticleSystem
import kotlinx.android.synthetic.main.exo_player_view.*
import kotlinx.android.synthetic.main.include_user_info.*
import kotlinx.android.synthetic.main.item_vlog.*
import kotlinx.android.synthetic.main.item_vlog.view.*
import kotlinx.android.synthetic.main.reactions_sheet.*
import kotlinx.android.synthetic.main.reactions_sheet.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

open class VideoFragment : AuthFragment() {

    private val exoPlayer: ExoPlayer by lazy { ExoPlayerFactory.newSimpleInstance(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.item_vlog, container, false)
    }

    protected fun stream(endpoint: String, decrypt_token: String) {
        val dataSourceFactory = DefaultHttpDataSourceFactory(requireContext().getString(R.string.app_name)).apply {
            defaultRequestProperties.set("Authorization", "Bearer=$decrypt_token")
        }
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
