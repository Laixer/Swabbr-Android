package com.laixer.swabbr.presentation.vloglist

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
import com.laixer.swabbr.presentation.model.UserVlogItem
import kotlinx.android.synthetic.main.fragment_vlog_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VlogListFragment : AuthFragment() {

    private val vm: VlogListViewModel by sharedViewModel()
    private val itemClick: (UserVlogItem) -> Unit = {
        findNavController().navigate(
            VlogListFragmentDirections.actionViewVlog(
                arrayOf(it.vlogId.toString()),
                it.vlogId.toString()
            )
        )
    }
    private val profileClick: (UserVlogItem) -> Unit = {
        findNavController().navigate(VlogListFragmentDirections.actionViewProfile(it.userId.toString()))
    }
    private var vlogListAdapter: VlogListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            vm.getRecommendedVlogs(refresh = true)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_vlog_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vlogListAdapter = VlogListAdapter(requireContext(), itemClick, profileClick)

        injectFeature()

        if (savedInstanceState == null) {
            vm.getRecommendedVlogs(refresh = false)
        }

        vlogsRecyclerView.adapter = vlogListAdapter

        vm.run {
            vlogs.observe(viewLifecycleOwner, Observer { updateVlogs(it) })
            swipeRefreshLayout.setOnRefreshListener { getRecommendedVlogs(refresh = true) }
        }
    }

    private fun updateVlogs(resource: Resource<List<UserVlogItem>?>) = with(resource) {
        with(swipeRefreshLayout) {
            when (state) {
                ResourceState.LOADING -> startRefreshing()
                ResourceState.SUCCESS -> {
                    stopRefreshing()
                    data?.let { vlogListAdapter?.submitList(it) }
                }
                ResourceState.ERROR -> {
                    stopRefreshing()
                    onError(resource)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vlogListAdapter = null
    }
}