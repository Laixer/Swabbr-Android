package com.laixer.sample.presentation.vlogdetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.laixer.presentation.visible
import com.laixer.sample.R
import com.laixer.sample.presentation.loadAvatar
import com.laixer.sample.presentation.model.ProfileVlogItem
import kotlinx.android.synthetic.main.include_user_info.view.userAvatar
import kotlinx.android.synthetic.main.include_user_info.view.userName
import kotlinx.android.synthetic.main.include_user_info.view.userUsername
import kotlinx.android.synthetic.main.item_vlog.view.player
import kotlinx.android.synthetic.main.overlay.view.isLive

class VideoView(layoutInflater: LayoutInflater, container: ViewGroup?) {
    val view: View =
        layoutInflater.inflate(R.layout.item_vlog, container, false)
    val overlayView = layoutInflater.inflate(R.layout.overlay, container, false)
    val exo: PlayerView = view.player
    var exoPlayer: ExoPlayer? = null

    fun bind(ePlayer: ExoPlayer?, item: ProfileVlogItem, context: Context) {
        exo.overlayFrameLayout?.addView(overlayView)
        exoPlayer = ePlayer
        exoPlayer?.setForegroundMode(false)
        exo.player = exoPlayer
        overlayView.userAvatar.loadAvatar(item.userId)
        overlayView.userUsername.text = context.getString(R.string.nickname, item.nickname)
        overlayView.userName.text = context.getString(R.string.full_name, item.firstName, item.lastName)

        if (item.isLive) {
            overlayView.isLive.visible()
            exo.useController = false
        } else {
            exo.showController()
        }
    }

    fun unbind() {
        exo.player = null
        exo.overlayFrameLayout?.removeAllViews()
    }
}
