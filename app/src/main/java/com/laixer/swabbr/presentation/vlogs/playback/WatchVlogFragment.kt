package com.laixer.swabbr.presentation.vlogs.playback

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.model.VlogLikeSummaryItem
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.video.VideoFragment
import com.plattysoft.leonids.ParticleSystem
import kotlinx.android.synthetic.main.exo_player_view.*
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.reactions_sheet.*
import kotlinx.android.synthetic.main.reactions_sheet.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

/**
 *  Fragment for watching a single vlog. This extends [VideoFragment]
 *  which contains the core playback functionality. This class manages
 *  the displaying of likes, reactions and other data about the vlog.
 *  Note that the playback of reactions is managed by TODO [].
 */
class WatchVlogFragment(id: String? = null) : VideoFragment() {
    private val vlogVm: VlogViewModel by viewModel()
    private val args by navArgs<WatchVlogFragmentArgs>()
    private val vlogId: UUID by lazy { UUID.fromString(id ?: args.vlogId) }

    /**
     *  Callback for when we click on a profile.
     */
    private val onProfileClick: (ReactionWrapperItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profile?userId=${it.user.id}"))
    }

    /**
     *  Callback for when we click on a given reaction.
     *  This takes us to a []
     */
    private val onReactionClick: (ReactionWrapperItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/watchReaction?reactionId=${it.reaction.id}"))
    }

    // TODO
    fun isVlogLiked(): Boolean = vlogVm.likes.value?.data?.users?.any { it.id == authUserVm.getAuthUserId() } ?: false

    /**
     *  Attaches observers to the [vlogVm] resources.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vlogVm.apply {
            vlog.observe(viewLifecycleOwner, Observer { onVlogLoaded(it) })
            reactions.observe(viewLifecycleOwner, Observer { onReactionsUpdated(it) })
            likes.observe(viewLifecycleOwner, Observer { onLikesUpdated(it) })
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     *  Binds all UI event listeners, then fetches the vlog
     *  with id [vlogId] including its reactions and likes.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reactions_sheet.run {
            reactionsRecyclerView.run {
                isNestedScrollingEnabled = false
                adapter = ReactionsAdapter(onProfileClick, onReactionClick)
            }
        }

        button_post_reaction.setOnClickListener {
            // if (!::vlogId.isInitialized) return@setOnClickListener TODO
            findNavController().navigate(WatchVlogFragmentDirections.actionRecordReaction("1", vlogId.toString()))
        }

        // Implement double tapping to like a vlog.
        // TODO Doesn't work
        video_player.setOnTouchListener { v, event ->
            GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    toggleLike(isVlogLiked())
                    return true
                }
            }).onTouchEvent(event)
            v.performClick()
        }

        val bottomSheet = BottomSheetBehavior.from(reactions_sheet).apply {
            addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    return
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    toggleButton.isChecked = newState != BottomSheetBehavior.STATE_COLLAPSED
                }
            })
        }

        toggleButton.apply {
            setOnClickListener {
                bottomSheet.state = when (bottomSheet.state) {
                    BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetBehavior.STATE_EXPANDED
                    else -> BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }

        like_button.setOnClickListener {
            // This might seem wrong, but because the checked state has priority over the click listener the
            // checked state is already flipped before we can check the state when the user initially clicked.
            // Because of this we have to interpret it in reverse.
            toggleLike(isVlogLiked())
        }

        /**
         *  Retrieve the actual vlog using the view model. When the
         *  vlog has been retrieved, [onVlogLoaded] will be called.
         *  A similar structure exists for the reactions and likes.
         */
        vlogVm.getVlog(vlogId)
        vlogVm.getReactions(vlogId)
        vlogVm.getVlogLikeSummary(vlogId)
    }

    /**
     *  If the current user has liked the vlog, unlike it
     *  and vice versa.
     *
     *  @param like True if we wish to like this, false for unliking.
     */
    private fun toggleLike(like: Boolean) {
        // if (!::vlogId.isInitialized) { return } TODO

        if (vlogVm.vlogs.value!!.data!!.first().user.id == authUserVm.getAuthUserId()) {
            like_button.isChecked = !like_button.isChecked
            return
        }
        like_button.isEnabled = false

        if (like) {
            vlogVm.unlike(vlogId)
        } else {
            vlogVm.like(vlogId)

            // Display the like icon.
            // TODO Move to helper.
            ParticleSystem(requireActivity(), 20, R.drawable.love_it_red, 1000)
                .setSpeedModuleAndAngleRange(0.1F, 0.2F, 240, 300)
                .setRotationSpeedRange(20F, 360F)
                .setScaleRange(1.5F, 1.6F)
                .setFadeOut(500)
                .oneShot(like_button, 1)
        }
    }

    /**
     *  Observes the [vlogVm] vog resource and starts playback
     *  when it loads. This also updates the UI.
     */
    private fun onVlogLoaded(res: Resource<VlogWrapperItem>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {
                content_loading_progressbar.visibility = View.VISIBLE
            }
            ResourceState.SUCCESS -> {
                // TODO Repair
//                user_avatar.loadAvatar(it.user.profileImage, it.user.id)
//                user_nickname.text = requireContext().getString(R.string.nickname, it.user.nickname)
//                user_username.text = requireContext().getString(R.string.full_name, it.user.firstName, it.user.lastName)

                data?.let {
                    stream(it.vlog.videoUri!!)
                }
            }
            ResourceState.ERROR -> {
                // TODO
            }
        }
    }

    /**
     *  Called when the [vlogVm] reactions resource changes.
     */
    private fun onReactionsUpdated(resource: Resource<List<ReactionWrapperItem>>) {
        with(resource) {
            when (state) {
                ResourceState.LOADING -> {
                    reactions_sheet.progressBar.visible()
                }
                ResourceState.SUCCESS -> {
                    reactions_sheet.progressBar.gone()
                    data?.let {
                        (reactionsRecyclerView?.adapter as ReactionsAdapter?)?.submitList(it)
                        reaction_count.text = "${it.count()}"
                    }

                    reactions_sheet.visibility = View.VISIBLE
                }
                ResourceState.ERROR -> {
                    reactions_sheet.progressBar.gone()
                }
            }
        }.also {
            val hasReactions = vlogVm.reactions.value?.data?.isNullOrEmpty() == false
            no_reactions.visibility = if (hasReactions) View.GONE else View.VISIBLE
            reaction_scroll_view.visibility = if (hasReactions) View.VISIBLE else View.GONE
        }
    }

    /**
     *  Called when the [vlogVm] likes resource changes.
     */
    private fun onLikesUpdated(resource: Resource<VlogLikeSummaryItem>) = with(resource) {
        when (state) {
            ResourceState.LOADING -> {
                like_button.isEnabled = false
            }
            ResourceState.SUCCESS -> {
                val isLiked = data?.users?.any { it.id == authUserVm.getAuthUserId() } ?: false
                like_button.isChecked = isLiked
                like_button.isEnabled = !isLiked

                like_button.isEnabled = true
                like_count.text = "${data?.totalLikes ?: 0}"
            }
            ResourceState.ERROR -> {
                like_button.isEnabled = true
                like_button.isChecked = !like_button.isChecked
            }
        }
    }

    companion object {
        fun create(vlogId: String): WatchVlogFragment {
            return WatchVlogFragment(vlogId)
        }
    }
}
