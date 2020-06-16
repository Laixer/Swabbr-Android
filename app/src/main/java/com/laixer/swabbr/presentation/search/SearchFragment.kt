package com.laixer.swabbr.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.UserItem
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchFragment : AuthFragment(), SearchView.OnQueryTextListener {

    private val vm: SearchViewModel by sharedViewModel()
    private var lastQuery = ""
    private var searchAdapter: SearchAdapter? = null
    private var currentPage = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = SearchAdapter(requireContext(), onClick)

        injectFeature()

        if (savedInstanceState == null) {
            search("", page = currentPage)
        }

        searchRecyclerView.apply {
            isNestedScrollingEnabled = false
            adapter = searchAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (canScrollVertically(1)) {
                        vm.search(lastQuery, page = currentPage + 1, refreshList = false)
                    }
                }
            })
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

    private fun search(query: String, page: Int, refreshList: Boolean = false) {
        lastQuery = query
        currentPage = page
        vm.search(query, page, refreshList = refreshList)
    }

    private val onClick: (UserItem) -> Unit = {
        findNavController().navigate(
            SearchFragmentDirections.actionViewProfile(it.id.toString())
        )
    }

    private fun updateUsers(resource: Resource<List<UserItem>?>) {
        resource.run {
            swipeRefreshLayout.run {
                when (state) {
                    ResourceState.LOADING -> startRefreshing()
                    ResourceState.SUCCESS -> stopRefreshing()
                    ResourceState.ERROR -> {
                        stopRefreshing()
                        super.onError(resource)
                    }
                }
            }
            data?.let {
                searchAdapter?.submitList(it)
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            search(it, page = 1, refreshList = true)
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
