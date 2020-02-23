package com.laixer.swabbr.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.model.ProfileItem
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    private val vm: SearchViewModel by viewModel()
    private var lastQuery = ""
    private var searchAdapter: SearchAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = SearchAdapter(context!!, onClick)

        injectFeature()

        if (savedInstanceState == null) {
            vm.getProfiles("")
        }

        searchRecyclerView.run {
            searchRecyclerView.isNestedScrollingEnabled = false
            searchRecyclerView.adapter = searchAdapter
        }

        vm.run {
            profiles.observe(viewLifecycleOwner, Observer { updateUsers(it) })
            swipeRefreshLayout.setOnRefreshListener { search(lastQuery) }
        }

        searchView.run {
            searchView.setOnQueryTextListener(this@SearchFragment)
            searchView.requestFocus()
        }
    }

    private fun search(query: String) {
        lastQuery = query
        vm.getProfiles(query)
    }

    private val onClick: (ProfileItem) -> Unit =
        { findNavController().navigate(SearchFragmentDirections.actionViewProfile(it.id)) }

    private fun updateUsers(resource: Resource<List<ProfileItem>?>) {
        resource.run {
            swipeRefreshLayout.run {
                when (state) {
                    ResourceState.LOADING -> startRefreshing()
                    ResourceState.SUCCESS -> stopRefreshing()
                    ResourceState.ERROR -> stopRefreshing()
                }
            }
            data?.let {
                searchAdapter?.submitList(it)
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            search(it)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchAdapter = null
    }
}
