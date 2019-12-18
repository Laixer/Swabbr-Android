package com.laixer.swabbr.presentation.vloglist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.laixer.navigation.features.SwabbrNavigation
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

    private val itemClick: (Pair<ProfileItem, VlogItem>) -> Unit =
        { startActivity(SwabbrNavigation.vlogDetails(vlogIds = arrayListOf(it.second.vlogId))) }

    private val profileClick: (Pair<ProfileItem, VlogItem>) -> Unit =
        { startActivity(SwabbrNavigation.profile(userId = it.first.id)) }

    private lateinit var adapter: VlogListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vlog_list)
        setSupportActionBar(findViewById(R.id.toolbar))
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
        when (resource.state) {
            ResourceState.LOADING -> swipeRefreshLayout.startRefreshing()
            ResourceState.SUCCESS -> swipeRefreshLayout.stopRefreshing()
            ResourceState.ERROR -> swipeRefreshLayout.stopRefreshing()
        }
        resource.data?.let { adapter.submitList(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_vloglist, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.search -> {
            startActivity(SwabbrNavigation.search())
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
