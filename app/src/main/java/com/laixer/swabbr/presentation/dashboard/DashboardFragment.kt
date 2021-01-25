package com.laixer.swabbr.presentation.dashboard

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.vlogs.list.VlogListAdapter
import com.laixer.swabbr.presentation.vlogs.list.VlogListViewModel
import kotlinx.android.synthetic.main.fragment_vlog_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 *  Fragment representing the user dashboard, displaying vlogs.
 */
class DashboardFragment : AuthFragment() {
    private val vm: VlogListViewModel by sharedViewModel()
    private var vlogListAdapter: VlogListAdapter? = null

    /**
     *  Handle for when we click on a vlog.
     */
    private val itemClick: (VlogWrapperItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profileWatchVlog?userId=${it.user.id}&initialVlogId=${it.vlog.id}"))
    }

    // TODO Is this correct?
    /**
     *  Handle for when we click on a profile.
     */
    private val profileClick: (VlogWrapperItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profile?userId=${it.user.id}"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            vm.getRecommendedVlogs(refresh = false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectFeature()

        vlogListAdapter = VlogListAdapter(vm, authUserVm, itemClick, profileClick)

        vlogsRecyclerView.adapter = vlogListAdapter  // MAKE SURE THIS HAPPENS BEFORE ADAPTER INSTANTIATION

        swipeRefreshLayout.setOnRefreshListener { vm.getRecommendedVlogs(refresh = true) }

        vm.run {
            vlogs.observe(viewLifecycleOwner, Observer(this@DashboardFragment::updateVlogsFromViewModel))
            getRecommendedVlogs(refresh = false)
        }
    }

    /**
     *  Called when the observed vlog collection resource is updated.
     *
     *  @param res The observed vlog collection resource
     */
    private fun updateVlogsFromViewModel(res: Resource<List<VlogWrapperItem>>) {
        when (res.state) {
            ResourceState.LOADING ->
                swipeRefreshLayout.startRefreshing()
            ResourceState.SUCCESS -> {
                swipeRefreshLayout.stopRefreshing()
                vlogListAdapter?.submitList(res.data)
                vlogListAdapter?.notifyDataSetChanged()
            }
            ResourceState.ERROR -> {
                swipeRefreshLayout.stopRefreshing()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vlogListAdapter = null
        vlogsRecyclerView?.adapter = null
    }
}
