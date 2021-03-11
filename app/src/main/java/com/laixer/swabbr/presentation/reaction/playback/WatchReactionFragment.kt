package com.laixer.swabbr.presentation.reaction.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.Player
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.onClickProfile
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.video.WatchVideoFragment
import com.laixer.swabbr.utils.loadAvatar
import kotlinx.android.synthetic.main.exo_player_view.*
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.video_info_overlay.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

/**
 *  Wrapper around a single [WatchVideoFragment] used for [ReactionItem] playback.
 */
class WatchReactionFragment(id: String? = null) : WatchVideoFragment() {
    private val args by navArgs<WatchReactionFragmentArgs>()
    private val reactionVm: ReactionViewModel by viewModel()
    private val reactionId by lazy { UUID.fromString(id ?: args.reactionId) }

    /**
     *  Assign observers to [reactionVm].
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        reactionVm.reaction.observe(viewLifecycleOwner, Observer { onReactionLoaded(it) })

        return layoutInflater.inflate(R.layout.fragment_video, container, false)
    }

    /**
     *  Gets the actual reaction.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the vlog stats overlay.
        vlog_info_overlay.gone()

        reactionVm.getReaction(reactionId)

        // Assign profile click if we have a profile
        reactionVm.reaction.value?.data?.let { wrapper ->
            view_clickable_video_user.setOnClickListener { onClickProfile().invoke(wrapper.user) }
        }
    }

    /**
     *  Callback function for when our [reactionVm] reaction resource updates.
     */
    private fun onReactionLoaded(res: Resource<ReactionWrapperItem>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {
                video_content_loading_icon.visibility = View.VISIBLE
            }
            ResourceState.SUCCESS -> {
                video_content_loading_icon.visibility = View.GONE
                data?.let {
                    user_profile_image.loadAvatar(it.user.profileImage, it.user.id)
                    video_user_nickname.text = requireContext().getString(R.string.nickname, it.user.nickname)

                    loadMediaSource(it.reaction.videoUri!!)
                }
            }
            ResourceState.ERROR -> {
                video_content_loading_icon.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val TAG = "VlogFragment"

        // TODO Look into this.
        fun create(reactionId: String): WatchReactionFragment {
            return WatchReactionFragment(reactionId)
        }
    }
}