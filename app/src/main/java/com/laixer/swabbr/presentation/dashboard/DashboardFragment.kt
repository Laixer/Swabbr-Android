package com.laixer.swabbr.presentation.dashboard

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.reduceDragSensitivity
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.utils.todosortme.gone
import com.laixer.swabbr.presentation.utils.todosortme.visible
import com.laixer.swabbr.presentation.video.WatchVideoFragmentAdapter
import com.laixer.swabbr.presentation.video.WatchVideoListFragment
import com.laixer.swabbr.presentation.vlogs.list.VlogListViewModel
import com.laixer.swabbr.presentation.vlogs.playback.WatchVlogFragmentAdapter
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import kotlinx.android.synthetic.main.fragment_video_view_pager.*
import kotlinx.android.synthetic.main.fragment_vlog_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *  Fragment representing the user dashboard, displaying vlogs.
 *  This uses the [WatchVideoListFragment] to display swipeable
 *  vlogs in fullscreen.
 */
class DashboardFragment : WatchVideoListFragment() {
    private val vlogListVm: VlogListViewModel by viewModel()

    /**
     *  Attaches the observers to the [vlogListVm] vlogs resource,
     *  then gets calls a get for the resource itself.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectFeature()

        // Reduce swipe sensitivity
        video_viewpager.reduceDragSensitivity()

        // TODO Repair, we don't have refresh abilities now
        // swipe_refresh_layout_watch_video_list.setOnRefreshListener { getData(true) }

        vlogListVm.vlogs.observe(viewLifecycleOwner, Observer { updateVlogsFromViewModel(it) })
    }

    private fun getData(refresh: Boolean = false) {
        vlogListVm.getRecommendedVlogs(refresh)
    }

    /**
     *  Use the [WatchVlogFragmentAdapter] as adapter for vlog playback.
     */
    override fun getWatchVideoFragmentAdapter(): WatchVideoFragmentAdapter = WatchVlogFragmentAdapter(
        fragment = this@DashboardFragment,
        vlogListResource = vlogListVm.vlogs
    )

    override fun onStart() {
        super.onStart()

        // TODO When to call this && isAuthenticated?
        //  We only want this to happen if we are authenticated, else we should be redirected to the login fragment
        getData(false)
    }

    /**
     *  Called when the observed vlog collection resource is updated.
     */
    private fun updateVlogsFromViewModel(res: Resource<List<VlogWrapperItem>>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {
                // swipe_refresh_layout_watch_video_list.startRefreshing()
            }

            ResourceState.SUCCESS -> {
                //swipe_refresh_layout_watch_video_list.stopRefreshing()

                video_viewpager.adapter?.notifyDataSetChanged()

//                // TODO Bad solution
//                // Only enable the swipe refresh layout if we have no vlogs to display.
//                if (res.data?.any() == true) {
//                    swipe_refresh_layout_watch_video_list.gone()
//                } else {
//                    swipe_refresh_layout_watch_video_list.visible()
//                }

                // Update empty collection text based on the result.
                if (res.data?.any() == true) {
                    text_display_empty_video_collection.gone()
                } else {
                    text_display_empty_video_collection.visible()
                    text_display_empty_video_collection.text =
                        requireContext().getString(R.string.empty_vlog_collection)
                }
            }
            ResourceState.ERROR -> {
                // swipe_refresh_layout_watch_video_list.stopRefreshing()

                showMessage("Error loading recommended vlogs")
            }
        }
    }
}
