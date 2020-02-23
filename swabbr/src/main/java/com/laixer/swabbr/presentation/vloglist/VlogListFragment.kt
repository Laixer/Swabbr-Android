package com.laixer.swabbr.presentation.vloglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.model.ProfileVlogItem
import kotlinx.android.synthetic.main.fragment_vlog_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VlogListFragment : Fragment() {

    private val vm: VlogListViewModel by viewModel()
    private val itemClick: (ProfileVlogItem) -> Unit =
        {
            findNavController().navigate(VlogListFragmentDirections.actionViewVlog(arrayOf(it.vlogId)))
        }
    private val profileClick: (ProfileVlogItem) -> Unit =
        {
            findNavController().navigate(VlogListFragmentDirections.actionViewProfile(it.userId))
        }
    private var vlogListAdapter: VlogListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_vlog_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vlogListAdapter = VlogListAdapter(context!!, itemClick, profileClick)

        injectFeature()

        if (savedInstanceState == null) {
            vm.get(refresh = false)
        }

        vlogsRecyclerView.adapter = vlogListAdapter

        vm.run {
            vlogs.observe(viewLifecycleOwner, Observer { updateVlogs(it) })
            swipeRefreshLayout.setOnRefreshListener { get(refresh = true) }
        }
    }

    private fun updateVlogs(resource: Resource<List<ProfileVlogItem>?>) =
        with(resource) {
            with(swipeRefreshLayout) {
                when (state) {
                    ResourceState.LOADING -> startRefreshing()
                    ResourceState.SUCCESS -> stopRefreshing()
                    ResourceState.ERROR -> stopRefreshing()
                }
            }
            data?.let { vlogListAdapter?.submitList(it.filter { vlog -> !vlog.isLive }) }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        vlogListAdapter = null
    }
}
