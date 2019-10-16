package com.laixer.sample.presentation.vloglist

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.laixer.navigation.features.SampleNavigation
import com.laixer.sample.R
import com.laixer.sample.injectFeature
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.sample.presentation.model.ProfileItem
import com.laixer.sample.presentation.model.VlogItem
import kotlinx.android.synthetic.main.activity_vlog_list.*
import org.koin.androidx.viewmodel.ext.viewModel
import android.app.SearchManager
import android.content.Context
import android.widget.SearchView


class VlogListActivity : AppCompatActivity() {

    private val vm: VlogListViewModel by viewModel()

    private val itemClick: (Pair<ProfileItem, VlogItem>) -> Unit =
        { startActivity(SampleNavigation.vlogDetails(vlogId = it.second.vlogId)) }
//    // Open profile
//    private val itemClick: (Pair<ProfileItem, VlogItem>) -> Unit =
//        { startActivity(SampleNavigation.profile(userId = it.first.id)) }
    private val adapter = VlogListAdapter(itemClick)
    private val snackBar by lazy {
        Snackbar.make(swipeRefreshLayout, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) { vm.get(refresh = true) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vlog_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        injectFeature()

        if (savedInstanceState == null) {
            vm.get(refresh = false)
        }

        vlogsRecyclerView.adapter = adapter

        vm.vlogs.observe(this, Observer { updateVlogs(it) })
        swipeRefreshLayout.setOnRefreshListener { vm.get(refresh = true) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar, menu)

//        val searchItem = menu.findItem(R.id.search)
//
//        val searchManager = this.getSystemService(Context.SEARCH_SERVICE) as SearchManager
//
//        var searchView: SearchView? = null
//        if (searchItem != null) {
//            searchView = searchItem.actionView as SearchView
//        }
//        if (searchView != null) {
//            searchView!!.setSearchableInfo(searchManager.getSearchableInfo(this.componentName))
//        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun updateVlogs(resource: Resource<List<Pair<ProfileItem, VlogItem>>>?) {
        resource?.let {
            when (it.state) {
                ResourceState.LOADING -> swipeRefreshLayout.startRefreshing()
                ResourceState.SUCCESS -> swipeRefreshLayout.stopRefreshing()
                ResourceState.ERROR -> swipeRefreshLayout.stopRefreshing()
            }
            it.data?.let { adapter.submitList(it) }
            it.message?.let { snackBar.show() }
        }
    }
}
