package com.laixer.swabbr.presentation.vlogs.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.video.WatchVideoFragmentAdapter
import com.laixer.swabbr.presentation.video.WatchVideoListFragment
import com.laixer.swabbr.presentation.vlogs.list.VlogListViewModel
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

        /** Gets the actual vlogs. The result is handled by [onVlogsUpdated]. */
        vlogListVm.getVlogsForUser(UUID.fromString(userId), refresh = true)
    }

    /**
     *  Assign the [WatchVlogFragmentAdapter] as adapter.
     */
    override fun getWatchVideoFragmentAdapter(): WatchVideoFragmentAdapter = WatchVlogFragmentAdapter(
        fragment = this@WatchUserVlogsFragment,
        vlogListResource = vlogListVm.vlogs
    )

    /**
     *  Called when the observed vlog list resource in [vlogListVm] changes.
     *  If we fail to load the vlogs, a back press is simulated.
     */
    private fun onVlogsUpdated(res: Resource<List<VlogWrapperItem>>) = with(res) {
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
}


