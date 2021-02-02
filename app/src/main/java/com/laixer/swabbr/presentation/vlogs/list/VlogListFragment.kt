package com.laixer.swabbr.presentation.vlogs.list

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
import kotlinx.android.synthetic.main.fragment_vlog_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

// TODO This will probably not be used anymore. We will only use a swipe viewpager for displaying a "list" of vlogs.
/**
 * Fragment displaying a list of vlogs using [VlogListAdapter].
 */
class VlogListFragment : AuthFragment() {

    private val vm: VlogListViewModel by sharedViewModel()
    private val itemClick: (VlogWrapperItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profileWatchVlog?userId=${it.user.id}&vlogId=${it.vlog.id}"))
    }
    private val profileClick: (VlogWrapperItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profile?userId=${it.user.id}"))

    }
    private var vlogListAdapter: VlogListAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_vlog_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectFeature()

        vlogListAdapter = VlogListAdapter(vm, itemClick, profileClick)

        vm.getRecommendedVlogs(refresh = true)

        vlogsRecyclerView.adapter = vlogListAdapter

        vm.run {
            vlogs.observe(viewLifecycleOwner, Observer { updateVlogs(it) })
            swipeRefreshLayout.setOnRefreshListener { getRecommendedVlogs(refresh = true) }
        }
    }

    private fun updateVlogs(resource: Resource<List<VlogWrapperItem>>) = with(resource) {
        with(swipeRefreshLayout) {
            when (state) {
                ResourceState.LOADING -> startRefreshing()
                ResourceState.SUCCESS -> {
                    stopRefreshing()
                    data?.let{ vlogListAdapter?.submitList(it) }
                }
                ResourceState.ERROR -> {
                    stopRefreshing()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vlogListAdapter = null
    }
}
