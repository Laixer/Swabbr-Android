package com.laixer.swabbr.presentation.vlogs.details

import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.laixer.swabbr.data.datasource.model.WatchLivestreamResponse
import com.laixer.swabbr.data.datasource.model.WatchVlogResponse
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.livestream.LivestreamViewModel
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.LikeListItem
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.UserVlogItem
import com.plattysoft.leonids.ParticleSystem
import kotlinx.android.synthetic.main.exo_player_view.*
import kotlinx.android.synthetic.main.include_user_info_reversed.view.*
import kotlinx.android.synthetic.main.item_vlog.*
import kotlinx.android.synthetic.main.item_vlog.view.*
import kotlinx.android.synthetic.main.reactions_sheet.*
import kotlinx.android.synthetic.main.reactions_sheet.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VlogFragment : AuthFragment() {

    private val vm: VlogDetailsViewModel by viewModel()
    private val liveVm: LivestreamViewModel by viewModel()
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var userVlogItem: UserVlogItem
    private var livestreamId: String? = null
    private var reactionsAdapter: ReactionsAdapter? = null
    private lateinit var gestureDetector: GestureDetector

    fun isVlogLiked(): Boolean {
        return vm.likes.value?.data?.usersMinified?.any { it.id == authenticatedUser.user.id } ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectFeature()

        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                toggleLike(isVlogLiked())
                return true
            }
        })

        exoPlayer = ExoPlayerFactory.newSimpleInstance(requireContext())

        reactionsAdapter = ReactionsAdapter(requireContext())

        with(requireArguments()) {
            userVlogItem = getSerializable(PROFILEVLOGITEM_KEY) as UserVlogItem
            livestreamId = getSerializable(LIVESTREAM_ID_KEY) as String?
        }
    }

    @JvmName("start_vlog")
    private fun start(res: Resource<WatchVlogResponse>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {
                // TODO: Add loading anim
            }
            ResourceState.SUCCESS -> {
                stream(data?.endpointUrl!!)
            }
            ResourceState.ERROR -> {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @JvmName("start_live")
    private fun start(res: Resource<WatchLivestreamResponse>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {
                // TODO: Add loading anim
            }
            ResourceState.SUCCESS -> {
                stream(data?.endpointUrl!!)
            }
            ResourceState.ERROR -> {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun stream(endpoint: String) {
        val dataSourceFactory: DataSource.Factory =
            DefaultHttpDataSourceFactory(Util.getUserAgent(requireContext(), "Swabbr"))
        val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
        val uri = Uri.parse(endpoint)
        val mediaSource = mediaSourceFactory.createMediaSource(uri)

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        layoutInflater.inflate(R.layout.item_vlog, container, false).apply {
            reversed_userAvatar.loadAvatar(userVlogItem.profileImage, userVlogItem.userId)
            reversed_userUsername.text = requireContext().getString(R.string.nickname, userVlogItem.nickname)
            reversed_userName.text =
                requireContext().getString(R.string.full_name, userVlogItem.firstName, userVlogItem.lastName)
        }

    private fun toggleLike(like: Boolean) {
        like_button.isEnabled = false

        if (like) {
            vm.unlike(userVlogItem.vlogId)
        } else {
            vm.like(userVlogItem.vlogId)

            ParticleSystem(requireActivity(), 20, R.drawable.love_it_red, 1000)
                .setSpeedModuleAndAngleRange(0.1F, 0.2F, 240, 300)
                .setRotationSpeedRange(20F, 360F)
                .setScaleRange(1.5F, 1.6F)
                .setFadeOut(500)
                .oneShot(like_button, 1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        player.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            v.performClick()
        }

        livestreamId?.let {
            liveVm.watchResponse.observe(viewLifecycleOwner, Observer { start(it) })
        } ?: run {
            vm.watchResponse.observe(viewLifecycleOwner, Observer { start(it) })
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

            like_button.setOnClickListener {
                // This might seem wrong, but because the checked state has priority over the click listener the
                // checked state is already flipped before we can check the state when the user initially clicked.
                // Because of this we have to interpret it in reverse.
                toggleLike(isVlogLiked())
            }
        }

        livestreamId?.let {
            liveVm.watch(it)
        } ?: run {
            vm.watch(userVlogItem.vlogId)
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
                val isLiked = data?.usersMinified?.any { it.id == authenticatedUser.user.id } ?: false
                like_button.isChecked = isLiked
                like_button.isEnabled = !isLiked

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

        private const val PROFILEVLOGITEM_KEY = "PROFILEVLOGITEM"
        private const val LIVESTREAM_ID_KEY = "LIVESTREAMIDKEY"

        fun create(item: UserVlogItem, livestreamId: String?): VlogFragment {
            val fragment = VlogFragment()
            val bundle = Bundle()
            bundle.putSerializable(PROFILEVLOGITEM_KEY, item)
            bundle.putString(LIVESTREAM_ID_KEY, livestreamId)
            fragment.arguments = bundle
            return fragment
        }
    }
}
