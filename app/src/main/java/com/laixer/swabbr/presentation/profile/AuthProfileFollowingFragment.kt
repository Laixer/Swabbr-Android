package com.laixer.swabbr.presentation.profile

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.search.UserAdapter
import kotlinx.android.synthetic.main.fragment_auth_profile_following.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AuthProfileFollowingFragment : AuthFragment() {

    private val profileVm: ProfileViewModel by sharedViewModel()
    private var userAdapter: UserAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_profile_following, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileVm.run {
            followingUsers.observe(viewLifecycleOwner, Observer { updateUsers(it) })
        }

        userAdapter = UserAdapter(requireContext(), onClick)
        followingRecyclerView.apply {
            isNestedScrollingEnabled = false
            adapter = userAdapter
        }

        swipeRefreshLayout.setOnRefreshListener { getUsers(true) }

        getUsers(true)
    }

    private val onClick: (UserItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profile?userId=${it.id}"))
    }

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

    fun getUsers(refresh: Boolean = false) {
        authUserVm.getAuthUserId()?.let {
            profileVm.getFollowing(it, refresh)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userAdapter = null
        followingRecyclerView?.adapter = null
    }

    internal companion object {
        const val TAG = "AuthProfileFragment"
    }
}
