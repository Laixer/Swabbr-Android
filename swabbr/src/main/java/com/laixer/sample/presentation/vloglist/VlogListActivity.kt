package com.laixer.swabbr.presentation.vloglist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.laixer.navigation.features.SampleNavigation
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.presentation.model.ProfileItem
import com.laixer.swabbr.presentation.model.VlogItem
import kotlinx.android.synthetic.main.activity_vlog_list.*
import org.koin.androidx.viewmodel.ext.viewModel

class VlogListActivity : AppCompatActivity() {

    private val vm: VlogListViewModel by viewModel()

    // Open vlogdetails
    private val itemClick: (Pair<ProfileItem, VlogItem>) -> Unit =
        { startActivity(SampleNavigation.vlogDetails(vlogIds = arrayListOf(it.second.vlogId))) }

    // Open profile
    private val profileClick: (Pair<ProfileItem, VlogItem>) -> Unit =
        { startActivity(SampleNavigation.profile(userId = it.first.id)) }

    private lateinit var adapter: VlogListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vlog_list)
        adapter = VlogListAdapter(baseContext, itemClick, profileClick)

        injectFeature()

        if (savedInstanceState == null) {
            vm.get(refresh = false)
        }

        vlogsRecyclerView.adapter = adapter

        vm.vlogs.observe(this, Observer { updateVlogs(it) })
        swipeRefreshLayout.setOnRefreshListener { vm.get(refresh = true) }
    }

    private fun updateVlogs(resource: Resource<List<Pair<ProfileItem, VlogItem>>?>) {
        resource?.let { res ->
            when (res.state) {
                ResourceState.LOADING -> swipeRefreshLayout.startRefreshing()
                ResourceState.SUCCESS -> swipeRefreshLayout.stopRefreshing()
                ResourceState.ERROR -> swipeRefreshLayout.stopRefreshing()
            }
            res.data?.let { adapter.submitList(it) }
        }
    }
}
