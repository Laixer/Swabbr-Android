package com.laixer.swabbr.presentation.vlogdetails

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.LikeListItem
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.UserVlogItem
import kotlinx.android.synthetic.main.exo_player_view.*
import kotlinx.android.synthetic.main.include_user_info_reversed.view.*
import kotlinx.android.synthetic.main.item_vlog.view.*
import kotlinx.android.synthetic.main.reactions_sheet.*
import kotlinx.android.synthetic.main.reactions_sheet.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VlogFragment : AuthFragment() {

    private val vm: VlogDetailsViewModel by viewModel()
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var userVlogItem: UserVlogItem
    private var reactionsAdapter: ReactionsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injectFeature()

        reactionsAdapter = ReactionsAdapter(requireContext())

        super.onCreate(savedInstanceState)
        userVlogItem = requireArguments().getSerializable(PROFILEVLOGITEM_KEY) as UserVlogItem
        val dataSourceFactory: DataSource.Factory =
            DefaultHttpDataSourceFactory(Util.getUserAgent(this.context, "Swabbr"))
        val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
        val uri = Uri.parse(userVlogItem.url.toURI().toString())
        val mediaSource = mediaSourceFactory.createMediaSource(uri)

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this.context).apply {
            prepare(mediaSource, true, false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        layoutInflater.inflate(R.layout.item_vlog, container, false).apply {
            reversed_userAvatar.loadAvatar(userVlogItem.profileImage, userVlogItem.userId)
            reversed_userUsername.text = requireContext().getString(R.string.nickname, userVlogItem.nickname)
            reversed_userName.text = requireContext().getString(
                R.string.full_name, userVlogItem.firstName, userVlogItem
                    .lastName
            )

            player.apply {
                controllerShowTimeoutMs = -1
                controllerHideOnTouch = false
                exoPlayer.setForegroundMode(false)
                player = exoPlayer
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        reactions_sheet.run {
            reactionsRecyclerView.run {
                isNestedScrollingEnabled = false
                adapter = reactionsAdapter
            }

            toggleButton.apply {
                setOnClickListener {
                    bottomSheet.state = when (bottomSheet.state) {
                        BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetBehavior.STATE_EXPANDED
                        else -> BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }

            react_button.setOnClickListener {
                // TODO: Implement react screen
            }

            like_button.apply {
                setOnClickListener {
                    isEnabled = false
                    // This might seem wrong, but because the checked state has priority over the click listener the
                    // checked state is already flipped before we can check the state when the user initially clicked.
                    // Because of this we have to interpret it in reverse.
                    if (isChecked) vm.like(userVlogItem.vlogId) else vm.unlike(userVlogItem.vlogId)
                }
            }
        }

        vm.run {
            reactions.observe(viewLifecycleOwner, Observer { updateReactions(it) })
            likes.observe(viewLifecycleOwner, Observer { updateLikes(it) })

            getReactions(userVlogItem.vlogId, refresh = true)
            getLikes(userVlogItem.vlogId)
        }
    }

    private fun updateLikes(resource: Resource<LikeListItem?>) = with(resource) {
        when (state) {
            ResourceState.LOADING -> {
                like_button.isEnabled = false
            }
            ResourceState.SUCCESS -> {
                like_button.isChecked = data?.usersMinified?.any { it.id == authenticatedUser.user.id } ?: false
                like_button.isEnabled =
                    !(data?.usersMinified?.any { it.id == authenticatedUser.user.id } ?: false)
                like_button.isEnabled = true
                like_count.text = "${data?.totalLikeCount ?: 0}"
            }
            ResourceState.ERROR -> {
                like_button.isEnabled = true
                like_button.isChecked = !like_button.isChecked
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateReactions(resource: Resource<List<ReactionItem>?>) =
        with(resource) {
            when (state) {
                ResourceState.LOADING -> {
                    reactions_sheet.progressBar.visible()
                }
                ResourceState.SUCCESS -> {
                    reactions_sheet.progressBar.gone()
                    data?.let {
                        reactionsAdapter?.submitList(it)
                        reaction_count.text = "${it.count()}"
                    }
                }
                ResourceState.ERROR -> {
                    reactions_sheet.progressBar.gone()
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }.also {
            val hasReactions = vm.reactions.value?.data?.isNullOrEmpty() == false
            no_reactions.visibility = if (hasReactions) View.GONE else View.VISIBLE
            reaction_scroll_view.visibility = if (hasReactions) View.VISIBLE else View.GONE
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
        reactionsAdapter = null
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
