package com.laixer.swabbr.presentation.vlogs.playback

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.reaction.ReactionsAdapter
import com.laixer.swabbr.presentation.video.WatchVideoFragment
import com.laixer.swabbr.utils.formatNumber
import com.laixer.swabbr.utils.loadAvatar
import com.plattysoft.leonids.ParticleSystem
import kotlinx.android.synthetic.main.exo_player_view.*
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.reactions_sheet.*
import kotlinx.android.synthetic.main.video_info_overlay.*
import kotlinx.android.synthetic.main.vlog_info_overlay.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

// TODO BUG java.lang.IllegalStateException: Fragment WatchVlogFragment{5c199a7} (3adedb84-c7a9-45ac-bf25-9df53bf0f9ee) f0} has null arguments
/**
 *  Fragment for watching a single vlog. This extends [WatchVideoFragment]
 *  which contains the core playback functionality. This class manages
 *  the displaying of likes, reactions and other data about the vlog.
 *  Note that the playback of reactions is managed by [].
 */
class WatchVlogFragment(id: String) : WatchVideoFragment() {
    private val vlogVm: VlogViewModel by viewModel()
    private val args by navArgs<WatchVlogFragmentArgs>()
    private val vlogId: UUID by lazy { UUID.fromString(id ?: args.vlogId) }

    // TODO This shouldn't be placed here.
    /**
     *  Callback for when we click on a reaction profile.
     */
    private val onVlogProfileClick: (userId: UUID) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profile?userId=${it}"))
    }

    // TODO This shouldn't be placed here.
    /**
     *  Callback for when we click on a reaction profile.
     */
    private val onReactionProfileClick: (ReactionWrapperItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profile?userId=${it.user.id}"))
    }

    /**
     *  Callback for when we click on a given reaction.
     *  This takes us to a []
     */
    private val onReactionClick: (ReactionWrapperItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/watchReaction?reactionId=${it.reaction.id}"))
    }

    /**
     *  Attaches observers to the [vlogVm] resources.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vlogVm.apply {
            vlog.observe(viewLifecycleOwner, Observer { onVlogLoaded(it) })
            reactions.observe(viewLifecycleOwner, Observer { onReactionsUpdated(it) })
            reactionCount.observe(viewLifecycleOwner, Observer { onReactionCountUpdated(it) })
            vlogLikeCount.observe(viewLifecycleOwner, Observer { onVlogLikeCountUpdated(it) })
            vlogLikedByCurrentUser.observe(viewLifecycleOwner, Observer { onLikedByCurrentUserUpdated(it) })
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     *  Binds all UI event listeners, then fetches the vlog
     *  with id [vlogId] including its reactions and likes.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup callback for the profile icon click.
        view_clickable_video_user.setOnClickListener {
            vlogVm.vlog.value?.data?.user?.let {
                    onVlogProfileClick.invoke(it.id)
            }
        }

        // TODO
        val bottomSheetBehavior = BottomSheetBehavior.from(constraint_layout_reactions_sheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        // Apply the reaction sheet adapter to the swipeable overlay.
        reactions_sheet.run {
            reactionsRecyclerView.run {
                isNestedScrollingEnabled = false
                adapter = ReactionsAdapter(onReactionProfileClick, onReactionClick)
            }
        }

        /**
         *  TODO This is a mess. The bottom sheet currently has a peek height to allow us
         *       swipe up for reaction display. The layout in which it exists extends the
         *       main layout by this peek height, so we can see the full "hidden" part of
         *       the [BottomSheetBehavior]. This is an absolute beunfix which existed in
         *       the original design and has been copied to "make it work". This has to be
         *       cleaned up, because this behavior is not maintainable and is not the
         *       intended use for such a sheet. Should we use gestures instead?
         *       See https://github.com/Laixer/Swabbr-Android/issues/135
         *
         *  When we try to hide the bottom sheet, set it back to collapsed again so we can
         *  swipe up to view the reactions.
         */
        bottomSheetBehavior.apply {
            addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == STATE_HIDDEN) {
                        bottomSheetBehavior.state = STATE_COLLAPSED
                    }
                }

                // Does nothing, required for this callback.
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    return
                }
            })
        }

        // TODO Bug
        //      java.lang.IllegalStateException: Page(s) contain a ViewGroup with a LayoutTransition
        //      (or animateLayoutChanges="true"), which interferes with the scrolling animation. Make
        //      sure to call getLayoutTransition().setAnimateParentHierarchy(false) on all ViewGroups
        //      with a LayoutTransition before an animation is started.

        // Takes us to a reaction recording fragment.
        button_post_reaction.setOnClickListener {
            // TODO Hard coded camera id.
            // TODO Remove call, was debug purpose
            val nc = findNavController()

            findNavController().navigate(WatchVlogFragmentDirections.actionRecordReaction("1", vlogId.toString()))
        }

        // Implement double tapping to like a vlog.
        // TODO Doesn't work, fix
//        video_player.setOnTouchListener { v, event ->
//            GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
//                override fun onDoubleTap(e: MotionEvent?): Boolean {
//                    toggleLike()
//                    return true
//                }
//            }) // TODO Probably incorrect     .onTouchEvent(event)
//
//            // TODO What does this do?
//            v.performClick()
//        }

        /**
         *  Disable the button until we have the vlog and the vlogLikedByUser
         *  resource. This is restored by the [tryEnableLikeButton] method.
         */
        button_vlog_like.isEnabled = false
        button_vlog_like.setOnClickListener { toggleLike() }

        // Prevent previous vlog data from being loaded
        vlogVm.clearVlogResources()

        /**
         *  Retrieve the actual vlog using the view model. When the
         *  vlog has been retrieved, [onVlogLoaded] will be called.
         *  A similar structure exists for the reactions and likes.
         */
        vlogVm.getVlog(vlogId)
        vlogVm.getReactions(vlogId)
        vlogVm.getReactionCount(vlogId)
        vlogVm.isVlogLikedByCurrentUser(vlogId)
    }

    /**
     *  If the current user has liked the vlog, unlike it and vice versa.
     *  This should only be called when we [canToggleLike], even though
     *  this checks itself as well.
     */
    private fun toggleLike() {
        if (!canToggleLike()) {
            return
        }

        // Perform the backend calls for the liking operation
        if (vlogVm.vlogLikedByCurrentUser.value?.data == true) {
            button_vlog_like.isChecked = false
            vlogVm.unlike(vlogId)
        } else {
            button_vlog_like.isChecked = true
            vlogVm.like(vlogId)

            // Display the like animation.
            // TODO Move to helper.
            ParticleSystem(requireActivity(), 20, R.drawable.ic_love_it_red, 1000)
                .setSpeedModuleAndAngleRange(0.1F, 0.2F, 240, 300)
                .setRotationSpeedRange(20F, 360F)
                .setScaleRange(1.5F, 1.6F)
                .setFadeOut(500)
                .oneShot(button_vlog_like, 1)
        }
    }

    /**
     *  Observes the [vlogVm] vog resource and starts playback
     *  when it loads. This also updates the UI.
     */
    private fun onVlogLoaded(resource: Resource<VlogWrapperItem>) = with(resource) {
        when (state) {
            ResourceState.LOADING -> {
                video_content_loading_icon.visible()
            }
            ResourceState.SUCCESS -> {
                video_content_loading_icon.gone()

                data?.let {
                    // Display the user info
                    it.user.let { user ->
                        video_user_profile_image.loadAvatar(user.profileImage, user.id)
                        // TODO Make extension function for this, we don't always have the first and last name.
                        video_user_displayed_name.text =
                            requireContext().getString(R.string.full_name, user.firstName, user.lastName)
                        video_user_nickname.text = requireContext().getString(R.string.nickname, user.nickname)
                    }

                    // Display the vlog info and start playback
                    it.vlog.let { vlog ->
                        // TODO Proper resource usage?
                        // TODO Put in helper or something, not here
                        text_view_video_date_created.text =
                            vlog.dateCreated.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
                        vlog_view_count.text = requireContext().formatNumber(vlog.views)

                        stream(vlog.videoUri!!)
                    }
                }

                // Enable the like button if we have the vlog and vlogLikedByUser resource.
                tryEnableLikeButton()
            }
            ResourceState.ERROR -> {
                video_content_loading_icon.gone()

                Toast.makeText(requireContext(), "Error loading vlog - ${resource.message}", Toast.LENGTH_SHORT).show()
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
                }
                ResourceState.SUCCESS -> {
                    data?.let {
                        // Push the new reactions to the reaction adapter
                        (reactionsRecyclerView?.adapter as ReactionsAdapter?)?.submitList(it)
                    }
                }
                ResourceState.ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        "Error loading reactions - ${resource.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.also {
            val hasReactions = vlogVm.reactions.value?.data?.isNullOrEmpty() == false
            no_reactions.visibility = if (hasReactions) View.GONE else View.VISIBLE
            reaction_scroll_view.visibility = if (hasReactions) View.VISIBLE else View.GONE
        }
    }

    /**
     *  Called when the [vlogVm] reactions count resource changes.
     */
    private fun onReactionCountUpdated(resource: Resource<Int>) {
        with(resource) {
            when (state) {
                ResourceState.LOADING -> {
                }
                ResourceState.SUCCESS -> {
                    data?.let {
                        // TODO Use string resource like all other places or make this the standard?
                        // Push the statistics to the statistics bar
                        vlog_reaction_count.text = "$it"
                    }
                }
                ResourceState.ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        "Error loading reaction count - ${resource.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     *  Called when the [vlogVm] like count resource changes.
     */
    private fun onVlogLikeCountUpdated(resource: Resource<Int>) = with(resource) {
        when (state) {
            ResourceState.LOADING -> {
                // TODO Trying this out, should we always do this empty and - format for numbers?
                vlog_like_count.text = ""
            }
            ResourceState.SUCCESS -> {
                vlog_like_count.text = requireContext().formatNumber(resource.data ?: 0)
            }
            ResourceState.ERROR -> {
                vlog_like_count.text = "-"
                Toast.makeText(requireContext(), "Error loading likes - ${resource.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     *  Called when the [vlogVm] likedByCurrentUser resource changes.
     *  This will indicate whether or not the current user has liked
     *  the vlog we are watching. Note that the vlog like button will
     *  remain disabled if we own the vlog.
     */
    private fun onLikedByCurrentUserUpdated(resource: Resource<Boolean>) = with(resource) {
        when (state) {
            ResourceState.LOADING -> {
                button_vlog_like.isEnabled = false
            }
            ResourceState.SUCCESS -> {
                // Enable the like button if we have the vlog and vlogLikedByUser resource.
                if (!button_vlog_like.isEnabled) {
                    tryEnableLikeButton()
                }

                button_vlog_like.isChecked = data!!
            }
            ResourceState.ERROR -> {
                button_vlog_like.isEnabled = false

                Toast.makeText(
                    requireContext(),
                    "Error loading liked by current user - ${resource.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     *  If we [canToggleLike], the [button_vlog_like] will be enabled.
     */
    private fun tryEnableLikeButton() {
        if (canToggleLike()) {
            button_vlog_like.isEnabled = true
        }
    }

    /**
     *  Returns true only if we have the [onVlogLoaded] and
     *  [onLikedByCurrentUserUpdated] resources, and if we
     *  don't own the current [onVlogLoaded] resource.
     */
    private fun canToggleLike(): Boolean =
        !(vlogVm.vlog.value?.data == null
            || vlogVm.vlogLikedByCurrentUser.value?.data == null
            || vlogVm.vlog.value!!.data!!.vlog.userId == authUserVm.getAuthUserId())

    companion object {
        fun create(vlogId: String): WatchVlogFragment {
            return WatchVlogFragment(vlogId)
        }
    }
}
