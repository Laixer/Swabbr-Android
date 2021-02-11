package com.laixer.swabbr.presentation.reaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.Player
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.video.WatchVideoFragment
import com.laixer.swabbr.utils.loadAvatar
import kotlinx.android.synthetic.main.exo_player_view.*
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.video_info_overlay.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

// TODO Profile image click
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
    }

    /**
     *  Go back to the vlog after playback has finished.
     */
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        // If we have a back stack in the parent fragment manager,
        // calling the activity back button will crash the app as
        // some fragment transactions are still pending.
        if (playbackState == Player.STATE_ENDED) {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                requireActivity().onBackPressed()
            }
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
                    video_user_profile_image.loadAvatar(it.user.profileImage, it.user.id)
                    video_user_displayed_name.text = it.user.getDisplayName()
                    video_user_nickname.text = requireContext().getString(R.string.nickname, it.user.nickname)

                    text_view_video_date_created.text =
                        it.reaction.dateCreated.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))

                    stream(it.reaction.videoUri!!)
                }
            }
            ResourceState.ERROR -> {
                video_content_loading_icon.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val TAG = "VlogFragment"

        fun create(reactionId: String): WatchReactionFragment {
            return WatchReactionFragment(reactionId)
        }
    }
}
