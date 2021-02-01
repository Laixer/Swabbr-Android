package com.laixer.swabbr.presentation.vlogs.details

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.*
import com.laixer.swabbr.utils.Utils
import com.laixer.swabbr.utils.loadAvatar
import com.plattysoft.leonids.ParticleSystem
import kotlinx.android.synthetic.main.exo_player_view.*
import kotlinx.android.synthetic.main.include_user_info.*
import kotlinx.android.synthetic.main.item_vlog.*
import kotlinx.android.synthetic.main.reactions_sheet.*
import kotlinx.android.synthetic.main.reactions_sheet.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

// TODO Explain better!
/**
 *  Fragment for vlog display with which the user can interact.
 */
open class InteractiveVideoFragment : VideoFragment() {
    protected val vlogVm: VlogDetailsViewModel by viewModel()
    private lateinit var vlogId: UUID

    /**
     *  Callback for when we click on a profile.
     */
    private val onProfileClick: (ReactionWrapperItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profile?userId=${it.user.id}"))
    }

    /**
     *  Callback for when we click on a given reaction.
     */
    private val onReactionClick: (ReactionWrapperItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/watchReaction?reactionId=${it.reaction.id}"))
    }

    fun isVlogLiked(): Boolean =
        vlogVm.likes.value?.data?.users?.any { it.id == authUserVm.getAuthUserId() } ?: false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vlogVm.apply {
            vlogs.observe(viewLifecycleOwner, Observer(this@InteractiveVideoFragment::updateVlog))
            reactions.observe(viewLifecycleOwner, Observer(this@InteractiveVideoFragment::updateReactions))
            likes.observe(viewLifecycleOwner, Observer(this@InteractiveVideoFragment::updateLikes))
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.enterFullscreen(requireActivity())

        reactions_sheet.run {
            reactionsRecyclerView.run {
                isNestedScrollingEnabled = false
                adapter = ReactionsAdapter(onProfileClick, onReactionClick)
            }
        }

        react_button.setOnClickListener {
            if (!::vlogId.isInitialized) return@setOnClickListener
            findNavController().navigate(WatchVlogFragmentDirections.actionRecordReaction("1", vlogId.toString()))
        }

        player.setOnTouchListener { v, event ->
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
    }

    override fun onResume() {
        super.onResume()
        Utils.enterFullscreen(requireActivity())
    }

    override fun onDetach() {
        super.onDetach()
        Utils.exitFullscreen(requireActivity())
    }

    private fun toggleLike(like: Boolean) {
        if (!::vlogId.isInitialized) return

        if (vlogVm.vlogs.value!!.data!!.first().user.id == authUserVm.getAuthUserId()) {
            like_button.isChecked = !like_button.isChecked
            return
        }
        like_button.isEnabled = false

        if (like) {
            vlogVm.unlike(vlogId)
        } else {
            vlogVm.like(vlogId)

            ParticleSystem(requireActivity(), 20, R.drawable.love_it_red, 1000)
                .setSpeedModuleAndAngleRange(0.1F, 0.2F, 240, 300)
                .setRotationSpeedRange(20F, 360F)
                .setScaleRange(1.5F, 1.6F)
                .setFadeOut(500)
                .oneShot(like_button, 1)
        }
    }

    private fun updateVlog(res: Resource<List<VlogWrapperItem>>) {
        when (res.state) {
            ResourceState.LOADING -> {
            }
            ResourceState.SUCCESS -> {
                res.data?.first()?.let {
                    vlogId = it.vlog.id
                    vlogVm.getReactions(vlogId)
                    vlogVm.getVlogLikeSummary(vlogId)

                    user_avatar.loadAvatar(it.user.profileImage, it.user.id)
                    user_nickname.text = requireContext().getString(R.string.nickname, it.user.nickname)
                    user_username.text =
                        requireContext().getString(R.string.full_name, it.user.firstName, it.user.lastName)

                    reactions_sheet.visibility = View.VISIBLE
                }

            }
            ResourceState.ERROR -> {
            }
        }
    }

    private fun updateLikes(resource: Resource<VlogLikeSummaryItem>) = with(resource) {
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

    private fun updateReactions(resource: Resource<List<ReactionWrapperItem>>) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        reactionsRecyclerView?.adapter = null
    }
}
