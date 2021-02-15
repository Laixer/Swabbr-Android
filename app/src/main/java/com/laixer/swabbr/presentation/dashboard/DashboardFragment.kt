package com.laixer.swabbr.presentation.dashboard

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.video.WatchVideoFragmentAdapter
import com.laixer.swabbr.presentation.video.WatchVideoListFragment
import com.laixer.swabbr.presentation.vlogs.list.VlogListViewModel
import com.laixer.swabbr.presentation.vlogs.playback.WatchVlogFragmentAdapter
import com.laixer.swabbr.utils.reduceDragSensitivity
import kotlinx.android.synthetic.main.fragment_video_view_pager.*
import kotlinx.android.synthetic.main.fragment_vlog_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

// TODO Question: this gets the vlog user and vlog like summary as well
//      for each vlog. We never display this information in this fragment
//      thus we make a lot of unnecessary backend calls. Change this?
/**
 *  Fragment representing the user dashboard, displaying vlogs.
 *  This uses the [WatchVideoListFragment] to display swipeable
 *  vlogs in fullscreen.
 */
class DashboardFragment : WatchVideoListFragment() {
    private val vlogListVm: VlogListViewModel by sharedViewModel()

    /**
     *  Gets the recommended vlogs from the [vlogListVm] right away.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            vlogListVm.getRecommendedVlogs(refresh = true)
        }
    }

    /**
     *  Attaches the observers to the [vlogListVm] vlogs resource,
     *  then gets calls a get for the resource itself.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectFeature()

        vlogListVm.vlogs.observe(viewLifecycleOwner, Observer { updateVlogsFromViewModel(it) })

        // Reduce swipe sensitivity
        video_viewpager.reduceDragSensitivity()
    }

    /**
     *  Use the [WatchVlogFragmentAdapter] as adapter for vlog playback.
     */
    override fun getWatchVideoFragmentAdapter(): WatchVideoFragmentAdapter = WatchVlogFragmentAdapter(
        fragment = this@DashboardFragment,
        vlogListResource = vlogListVm.vlogs
    )

    /**
     *  Called when the observed vlog collection resource is updated.
     */
    private fun updateVlogsFromViewModel(res: Resource<List<VlogWrapperItem>>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {
            }
            ResourceState.SUCCESS -> {
                video_viewpager.adapter?.notifyDataSetChanged()

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
                Toast.makeText(requireContext(), "Error loading recommended vlogs", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
