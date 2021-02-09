package com.laixer.swabbr.presentation.search

import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.laixer.swabbr.domain.types.Pagination
import com.laixer.swabbr.domain.types.SortingOrder
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.UserItem
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 *  Fragment that handles our user searching.
 */
class SearchFragment : AuthFragment(), SearchView.OnQueryTextListener {
    private val vm: SearchViewModel by sharedViewModel()
    private var userAdapter: UserAdapter? = null
    private var currentPage: Int = 1
    private var lastQuery: String = ""

    /**
     *  Callback for when we click a user item.
     */
    private val onClickUser: (UserItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profile?userId=${it.id}"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectFeature()

        userAdapter = UserAdapter(requireContext(), onClickUser)

        searchRecyclerView.apply {
            isNestedScrollingEnabled = false
            adapter = userAdapter

            // Add a listener to respond to a scroll event
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (recyclerView.canScrollVertically(1) && vm.lastQueryResultCount > 0) {
                        search(page = currentPage++)
                    }
                }
            })
        }

        swipeRefreshLayout.setOnRefreshListener { search(lastQuery) }

        searchView.run {
            search(lastQuery, 1, true)
            searchView.setOnQueryTextListener(this@SearchFragment)
            searchView.requestFocus()

            // Clear the results when we click the X in the search bar
            searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn)?.setOnClickListener {
                vm.clearSearchResults()
                // Also remove the actual search query
                // TODO Call the existing click listener
                searchView.setQuery("", false)
            }
        }

        vm.run {
            profiles.observe(viewLifecycleOwner, Observer(this@SearchFragment::updateUsers))
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun search(
        query: String = lastQuery,
        page: Int = currentPage,
        refreshList: Boolean = true
    ): Boolean {
        // Can't search negative pages
        if (currentPage < 1) {
            Log.e(TAG, "page index must be 1 or higher, received '$page'")
            return false
        }

        // Only search if 3 or more characters are queried
        if (query.length < 3) {
            Log.e(TAG, "query must consist of 3 characters or more, received '$query' (size: ${query.length})")
            return false
        }

        val limit = 25
        lastQuery = query
        currentPage = page
        vm.search(
            query = query, pagination = Pagination(
                sortingOrder = SortingOrder.DESCENDING,
                limit = limit,
                offset = (page - 1) * limit
            ), refreshList = refreshList
        )
        return true
    }

    /**
     *  Called when the observed user search result
     *  resource in [vm] changes.
     */
    private fun updateUsers(resource: Resource<List<UserItem>>) {
        resource.run {
            swipeRefreshLayout.run {
                when (state) {
                    ResourceState.LOADING -> startRefreshing()
                    ResourceState.SUCCESS -> {
                        stopRefreshing()
                        data?.let { userAdapter?.submitList(it) }
                    }
                    ResourceState.ERROR -> {
                        stopRefreshing()
                    }
                }
            }
        }
    }

    override fun onQueryTextChange(query: String): Boolean {
        return search(query = query, page = 1, refreshList = true)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return search(query = query, page = 1, refreshList = true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchRecyclerView.adapter = null
        userAdapter = null
    }

    companion object {
        private const val TAG = "SearchFragment"
    }
}
