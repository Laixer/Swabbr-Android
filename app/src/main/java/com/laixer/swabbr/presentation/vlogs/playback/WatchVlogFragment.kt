package com.laixer.swabbr.presentation.vlogs.playback

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
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.getUuidOrNull
import com.laixer.swabbr.extensions.onClickProfile
import com.laixer.swabbr.extensions.putUuid
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.reaction.playback.ReactionsAdapter
import com.laixer.swabbr.presentation.utils.buildDoubleTapListener
import com.laixer.swabbr.presentation.utils.todosortme.gone
import com.laixer.swabbr.presentation.utils.todosortme.visible
import com.laixer.swabbr.presentation.video.WatchVideoFragment
import com.laixer.swabbr.utils.formatNumber
import com.laixer.swabbr.utils.loadAvatarFromUser
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import com.plattysoft.leonids.ParticleSystem
import kotlinx.android.synthetic.main.exo_player_view.*
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.reactions_sheet.*
import kotlinx.android.synthetic.main.video_info_overlay.*
import kotlinx.android.synthetic.main.vlog_info_overlay.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

// TODO Pressing back, then doing anything, then re-entering Swabbr makes
//      us re-enter this fragment, attempting to start vlogging again.
/**
 *  Fragment for watching a single vlog. This extends [WatchVideoFragment]
 *  which contains the core playback functionality. This class manages
 *  the displaying of likes, reactions and other data about the vlog.
 *  Note that the playback of reactions is managed by [].
 */
class WatchVlogFragment : WatchVideoFragment() {
    private val args by navArgs<WatchVlogFragmentArgs>()
    private val vlogVm: VlogViewModel by viewModel()

    private val vlogId: UUID by lazy {
        arguments?.getUuidOrNull(BUNDLE_KEY_VLOG_ID) ?: UUID.fromString(args.vlogId)
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
            vlogVm.vlog.value?.data?.user?.let { onClickProfile().invoke(it) }
        }

        // Apply the reaction sheet adapter to the swipeable overlay.
        reactions_sheet.run {
            reactionsRecyclerView.run {
                isNestedScrollingEnabled = false
                adapter = ReactionsAdapter(
                    currentUserId = getSelfId(),
                    onProfileClick = onClickProfile(),
                    onReactionClick = onReactionClick,
                    onDeleteClick = onReactionDeleteClick
                )
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
        val bottomSheetBehavior = BottomSheetBehavior.from(constraint_layout_reactions_sheet)
        bottomSheetBehavior.state = STATE_COLLAPSED

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

        // Takes us to a reaction recording fragment.
        // TODO Why doesn't this work explicitly, only global? Just like watch reaction...
        button_post_reaction.setOnClickListener {
            findNavController().navigate(
                WatchVlogFragmentDirections.actionGlobalRecordReactionFragment(
                    targetVlogId = vlogId.toString()
                )
            )
        }

        // Double tap to like a vlog.
        val doubleTapListener = buildDoubleTapListener(requireActivity(), ::toggleLike)
        // TODO We attach this behaviour to the reaction sheet. This is a temp fix.
        //  After the following issue has been fixed change to video_player.setOnTouchListener { ... }.
        //  https://github.com/Laixer/Swabbr-Android/issues/135
        coordinator_layout_reactions_sheet.setOnTouchListener { _, event ->
            doubleTapListener.onTouchEvent(event)

            // Required to call according to Android. Our view must know there was a click.
            view.performClick()
        }

        /**
         *  Disable the button until we have the vlog and the vlogLikedByUser
         *  resource. This is restored by the [tryEnableLikeButton] method.
         */
        button_vlog_like.isEnabled = false
        button_vlog_like.setOnClickListener { toggleLike() }
    }

    // TODO Merge to single call.
    /**
     *  Get the vlog and its additional data.
     */
    override fun getData(refresh: Boolean) {
        vlogVm.getVlog(vlogId)
        vlogVm.getReactions(vlogId)
        vlogVm.isVlogLikedByCurrentUser(vlogId)
    }

    // TODO Is this behaviour correct? I think not... Change to on vlog finished?
    // FUTURE Trigger only when we finish watching a vlog?
    /**
     *  Called each time we enter this fragment. This adds a
     *  view to the corresponding vlog with id [vlogId].
     */
    override fun onResume() {
        super.onResume()

        vlogVm.addView(vlogId)
    }

    // TODO Explicit action doesn't work, global does... WHY
    /**
     *  Callback for when we click on a given reaction.
     */
    private val onReactionClick: (ReactionWrapperItem) -> Unit = {
        findNavController().navigate(
            WatchVlogFragmentDirections.actionGlobalWatchReactionsForVlogFragment(
                vlogId = vlogId.toString(), // TODO Can we cast all these params to uuid?
                initialReactionId = it.reaction.id.toString()
            )
        )
    }

    /**
     *  Callback for when we click the delete icon of a reaction.
     *  Note that this will only work if we own the reaction. This
     *  check is performed by the [vlogVm] so this can always be
     *  called safely.
     */
    private val onReactionDeleteClick: (ReactionWrapperItem) -> Unit = {
        vlogVm.deleteReaction(it.reaction)
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
            ParticleSystem(requireActivity(), 20, R.drawable.ic_love_it, 1000)
                .setSpeedModuleAndAngleRange(0.1F, 0.2F, 240, 300)
                .setRotationSpeedRange(20F, 360F)
                .setScaleRange(1.2F, 1.4F)
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

                clearErrorIfPresent()
            }
            ResourceState.SUCCESS -> {
                video_content_loading_icon.gone()

                data?.let {
                    // Display the user info
                    user_profile_image.loadAvatarFromUser(it.user)
                    video_user_nickname.text = requireContext().getString(R.string.nickname, it.user.nickname)

                    // Display the vlog info and start playback
                    vlog_view_count.text = requireContext().formatNumber(it.vlog.views)

                    loadMediaSource(it.vlog.videoUri!!)
                }

                // Enable the like button if we have the vlog and vlogLikedByUser resource.
                tryEnableLikeButton()
            }
            ResourceState.ERROR -> {
                video_content_loading_icon.gone()

                // Notify the user that we can't load the vlog.
                onResourceError(R.string.error_load_vlog)

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
                    showMessage("Could not load reactions")
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
                // If we reach this, we either couldn't load the vlog like state in the
                // first place or our vlog like toggle has failed. Act accordingly.
                button_vlog_like.isChecked = resource.data ?: false

                // This doesn't handle whether or not the button is enabled,
                // reaching this point said operation should already be done.

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
            || vlogVm.vlog.value!!.data!!.vlog.userId == authVm.getSelfIdOrNull())

    companion object {
        private val TAG = WatchVlogFragment::class.java.simpleName
        private const val BUNDLE_KEY_VLOG_ID = "vlogId"

        /**
         *  Static strong-typed constructor.
         */
        fun newInstance(vlogId: UUID) = WatchVlogFragment().apply {
            arguments = Bundle().apply { putUuid(BUNDLE_KEY_VLOG_ID, vlogId) }
        }
    }
}
