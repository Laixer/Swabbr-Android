package com.laixer.swabbr.presentation.reaction.list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.goBack
import com.laixer.swabbr.extensions.reduceDragSensitivity
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.reaction.playback.WatchReactionFragmentAdapter
import com.laixer.swabbr.presentation.types.VideoPlaybackState
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.presentation.utils.todosortme.gone
import com.laixer.swabbr.presentation.utils.todosortme.visible
import com.laixer.swabbr.presentation.video.WatchVideoFragmentAdapter
import com.laixer.swabbr.presentation.video.WatchVideoListFragment
import com.laixer.swabbr.utils.resources.ResourceState
import kotlinx.android.synthetic.main.fragment_video_view_pager.*
import kotlinx.android.synthetic.main.fragment_vlog_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

/**
 *  Fragment representing the user dashboard, displaying vlogs.
 *  This uses the [WatchVideoListFragment] to display swipeable
 *  vlogs in fullscreen.
 */
class WatchReactionsForVlogFragment : WatchVideoListFragment() {
    private val reactionListVm: ReactionListViewModel by viewModel()

    private val args by navArgs<WatchReactionsForVlogFragmentArgs>()
    private val vlogId by lazy { UUID.fromString(args.vlogId) } // TODO Unsafe
    private val initialReactionId by lazy { UUID.fromString(args.initialReactionId) } // TODO Unsafe

    /**
     *  Attaches the observers to the [reactionListVm] vlogs resource,
     *  then gets calls a get for the resource itself.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectFeature()

        // Reduce swipe sensitivity
        video_viewpager.reduceDragSensitivity()

        // TODO Repair, we don't have refresh abilities now
        // swipe_refresh_layout_watch_video_list.setOnRefreshListener { getData(true) }

        reactionListVm.reactions.observe(viewLifecycleOwner, Observer { onReactionsResourceChanged(it) })
    }

    /**
     *  Gets our reactions for our vlog.
     */
    private fun getData(refresh: Boolean = false) {
        reactionListVm.getReactionsForVlog(vlogId, refresh)
    }

    /**
     *  Use the [WatchReactionFragmentAdapter] as adapter for reaction playback.
     */
    override fun getWatchVideoFragmentAdapter(): WatchVideoFragmentAdapter = WatchReactionFragmentAdapter(
        fragment = this@WatchReactionsForVlogFragment,
        reactionListResource = reactionListVm.reactions,
        onVideoCompletedCallback = ::onVideoPlaybackStateChanged
    )

    /**
     *  Triggers the data get.
     */
    override fun onStart() {
        super.onStart()

        // TODO When to call this && isAuthenticated?
        //  We only want this to happen if we are authenticated, else we should be redirected to the login fragment
        getData(false)
    }

    /**
     *  Go to the next reaction if one finishes playback. If we don't have
     *  any new ones, go back to the [vlogId]. This callback is subscribed
     *  and managed by the adapter created in [getWatchVideoFragmentAdapter].
     *  This class doesn't need to do anything with regards to subscription.
     *
     *  @param reactionId The reaction that ended playback.
     *  @param position The position in the [video_viewpager].
     *  @param videoPlaybackState The new playback state.
     */
    private fun onVideoPlaybackStateChanged(reactionId: UUID, position: Int, videoPlaybackState: VideoPlaybackState) {
        if (videoPlaybackState == VideoPlaybackState.FINISHED) {
            video_viewpager.adapter?.let { adapter ->
                if (position >= adapter.itemCount - 1) {
                    // Go back if we reached the last item (position starts at 0).
                    goBack()
                } else {
                    // Go to the next item if we have more items.
                    video_viewpager.currentItem = position + 1
                }
            }
        }
    }

    /**
     *  Called when the observed reaction collection resource is updated.
     */
    private fun onReactionsResourceChanged(res: Resource<List<ReactionWrapperItem>>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {
                // swipe_refresh_layout_watch_video_list.startRefreshing()
            }

            ResourceState.SUCCESS -> {
                //swipe_refresh_layout_watch_video_list.stopRefreshing()
                video_viewpager.adapter?.notifyDataSetChanged()

                // Instantly jump to the initial reaction, or the first if we can't find it.
                video_viewpager.currentItem =
                    res.data?.indexOf(res.data?.firstOrNull { x -> x.reaction.id == initialReactionId }) ?: 0

                // Update empty collection text based on the result.
                if (res.data?.any() == true) {
                    text_display_empty_video_collection.gone()
                } else {
                    text_display_empty_video_collection.visible()
                    text_display_empty_video_collection.text =
                        requireContext().getString(R.string.empty_reaction_collection)
                }
            }
            ResourceState.ERROR -> {
                // swipe_refresh_layout_watch_video_list.stopRefreshing()

                showMessage("Error loading reactions for vlog")
            }
        }
    }
}
