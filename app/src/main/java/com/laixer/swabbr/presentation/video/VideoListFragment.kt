package com.laixer.swabbr.presentation.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.vlogs.list.VlogListViewModel
import com.laixer.swabbr.presentation.vlogs.playback.WatchUserVlogsFragmentArgs
import com.laixer.swabbr.presentation.vlogs.playback.WatchVlogFragmentAdapter
import kotlinx.android.synthetic.main.fragment_video_view_pager.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

// TODO What to display if we have no content?
// TODO Fix position storing, not sure this works correctly.
/**
 *  This fragment is used to watch a video and to be able to swipe left
 *  and right to go to other videos. When entering this fragment, the
 *  entire fragment is filled with the [video_viewpager].
 *
 *  Note that this uses a view pager with id [video_viewpager] to handle
 *  swiping left and right. Each video itself is then inflated using a
 *  [FragmentVideoAdapter].
 *
 *  Note that we enter this by a deeplink in [nav_graph_vlogs] by the
 *  to take us to the id [watch_user_vlogs_dest].
 */
open class VideoListFragment(
    /** Used to inflate each video file when displayed. */
    private val watchVideoFragmentAdapter: WatchVideoFragmentAdapter
) : AuthFragment() {
    private val vlogListVm: VlogListViewModel by viewModel()
    private val args by navArgs<WatchUserVlogsFragmentArgs>()
    private val userId by lazy { args.userId }
    private val initialVlogId by lazy { args.initialVlogId }

    /**
     *  Attaches observer to the [vlogListVm] vlogs resource.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vlogListVm.vlogs.observe(viewLifecycleOwner, Observer { onVlogsUpdated(it) })
        return inflater.inflate(R.layout.fragment_video_view_pager, container, false)
    }

    /**
     *  Sets up the UI.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** Use a [WatchVlogFragmentAdapter] to display each vlog. */
        video_viewpager.apply {
            adapter = watchVideoFragmentAdapter
        }

        /** Gets the actual vlogs. The result is handled by [onVlogsUpdated]. */
        vlogListVm.getVlogsForUser(UUID.fromString(userId), refresh = true)
    }

    /**
     *  Called when the observed vlog list resource in [vlogListVm] changes.
     *  If we fail to load the vlogs, a back press is simulated.
     */
    private fun onVlogsUpdated(resource: Resource<List<VlogWrapperItem>>) = with(resource) {
        when (state) {
            ResourceState.LOADING -> {
                // TODO
            }
            ResourceState.SUCCESS -> {
                // TODO Look at this
                data?.let {
                    initialVlogId?.let {
                        video_viewpager.currentItem =
                            vlogListVm.vlogs.value?.data?.indexOf(vlogListVm.vlogs.value?.data?.first { item ->
                                item.vlog.id.toString() == it
                            }) ?: 0
                    }

                    video_viewpager.adapter?.notifyDataSetChanged()
                }
            }
            ResourceState.ERROR -> {
                requireActivity().onBackPressed()
            }
        }
    }

    /**
     *  Called when we re-enter this fragment. This is used to
     *  store the vlog which we were watching before.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        with(savedInstanceState?.getInt(CURRENT_ITEM_INDEX) ?: 0) {
            video_viewpager.currentItem = this
        }

        super.onViewStateRestored(savedInstanceState)
    }

    /**
     *  Called when we exit this fragment. This stores the
     *  vlog we are currently watching.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        // TODO This used to crash saying vlog_viewpager was
        //  null. This call was added to save this, some bugs
        //  might originate here though.
        if (video_viewpager == null) {
            return
        }

        outState.putInt(CURRENT_ITEM_INDEX, video_viewpager.currentItem)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        video_viewpager.adapter = null
    }

    companion object {
        private const val CURRENT_ITEM_INDEX = "CURRENTITEMINDEX"
        private const val TAG = "WatchUserVlogsFragment"
    }
}


