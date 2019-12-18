package com.laixer.swabbr.presentation.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.model.ProfileItem
import kotlinx.android.synthetic.main.activity_profile.swipeRefreshLayout
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.androidx.viewmodel.ext.viewModel

class SearchActivity : AppCompatActivity() {

    private val vm: SearchViewModel by viewModel()
    private lateinit var adapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = SearchAdapter(baseContext)
        setContentView(R.layout.activity_search)
        setSupportActionBar(findViewById(R.id.toolbar))

        injectFeature()

        searchRecyclerView.isNestedScrollingEnabled = false
        searchRecyclerView.adapter = adapter

        vm.profiles.observe(this, Observer { updateUsers(it) })

        toolbar.getChildAt(0).requestFocus()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            search(query)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_search, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            clearFocus()
        }
        return true
    }

    private fun search(query: String) {
        vm.getProfiles(query)
    }

    private fun updateUsers(resource: Resource<List<ProfileItem>?>) {
        when (resource.state) {
            ResourceState.LOADING -> swipeRefreshLayout.startRefreshing()
            ResourceState.SUCCESS -> swipeRefreshLayout.stopRefreshing()
            ResourceState.ERROR -> swipeRefreshLayout.stopRefreshing()
        }
        resource.data?.let { adapter.submitList(it) }
    }
}
