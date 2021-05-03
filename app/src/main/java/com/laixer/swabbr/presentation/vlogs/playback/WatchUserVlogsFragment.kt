package com.laixer.swabbr.presentation.vlogs.playback

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.laixer.swabbr.extensions.goBack
import com.laixer.swabbr.extensions.reduceDragSensitivity
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.types.VideoPlaybackState
import com.laixer.swabbr.presentation.video.WatchVideoFragmentAdapter
import com.laixer.swabbr.presentation.video.WatchVideoListFragment
import com.laixer.swabbr.presentation.vlogs.list.VlogListViewModel
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_video_view_pager.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

/**
 *  This fragment is used to watch vlogs that belong to a given user.
 *  See [WatchVideoListFragment] for more explanation.
 *
 *  Note that we enter this by a deeplink in [nav_graph_vlogs] by the
 *  to take us to the id [watch_user_vlogs_dest].
 */
class WatchUserVlogsFragment : WatchVideoListFragment() {
    private val vlogListVm: VlogListViewModel by viewModel()
    private val args by navArgs<WatchUserVlogsFragmentArgs>()
    private val userId by lazy { args.userId }
    private val initialVlogId by lazy { args.initialVlogId }

    /**
     *  Attaches observer to the [vlogListVm] vlogs resource.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vlogListVm.vlogs.observe(viewLifecycleOwner, Observer { onVlogsUpdated(it) })

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     *  Triggers a get for the [vlogListVm] resources.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        video_viewpager.reduceDragSensitivity()
    }

    override fun getData(refresh: Boolean) {
        vlogListVm.getVlogsForUser(UUID.fromString(userId), refresh = true)
    }

    /**
     *  Assign the [WatchVlogFragmentAdapter] as adapter.
     */
    override fun getWatchVideoFragmentAdapter(): WatchVideoFragmentAdapter = WatchVlogFragmentAdapter(
        fragment = this@WatchUserVlogsFragment,
        vlogListResource = vlogListVm.vlogs,
        onVideoCompletedCallback = ::onVideoPlaybackStateChanged
    )

    /**
     *  Go to the next vlog if one finishes playback. This callback is subscribed
     *  and managed by the adapter created in [getWatchVideoFragmentAdapter].
     *  This class doesn't need to do anything with regards to subscription.
     *
     *  @param vlogId The vlog that ended playback.
     *  @param position The position in the [video_viewpager].
     *  @param videoPlaybackState The new playback state.
     */
    private fun onVideoPlaybackStateChanged(vlogId: UUID, position: Int, videoPlaybackState: VideoPlaybackState) {
        if (videoPlaybackState == VideoPlaybackState.FINISHED) {
            video_viewpager.adapter?.let { adapter ->
                if (position < adapter.itemCount - 1) {
                    // Go to the next item if we have more items.
                    video_viewpager.currentItem = position + 1
                }
            }
        }
    }

    // TODO Pull up to WatchVideoListFragment
    /**
     *  Called when the observed vlog list resource in [vlogListVm] changes.
     *  If we fail to load the vlogs, a back press is simulated.
     */
    private fun onVlogsUpdated(res: Resource<List<VlogWrapperItem>>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {
                // Clear the adapter if we went to another user.
                // video_viewpager.adapter?.
            }
            ResourceState.SUCCESS -> {
                data?.let {
                    val initialItemIndex =
                        vlogListVm.vlogs.value?.data?.indexOf(vlogListVm.vlogs.value?.data?.first { item ->
                            item.vlog.id.toString() == initialVlogId
                        })

                    // Assign index based on if we found the desired item in our list.
                    val selectedItemIndex = if (initialItemIndex == null) {
                        Log.w(TAG, "Could not find index of vlog with id $initialVlogId")
                        0
                    } else {
                        initialItemIndex
                    }

                    // Don't animate, just go there instantly.
                    video_viewpager.setCurrentItem(selectedItemIndex, false)

                    video_viewpager.adapter?.notifyDataSetChanged()
                }
            }
            ResourceState.ERROR -> {
                // TODO When does this trigger
                showMessage("Error getting user vlogs")

                findNavController().popBackStack()
            }
        }
    }
}
