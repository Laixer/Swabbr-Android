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
import com.laixer.swabbr.presentation.auth.AuthUserViewModel
import com.laixer.swabbr.presentation.model.FollowRequestItem
import com.laixer.swabbr.presentation.model.UserItem
import kotlinx.android.synthetic.main.fragment_auth_profile_following.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 *  Fragment displaying incoming follow requests which
 *  can either be accepted or declined.
 */
class AuthProfileRequestsFragment : AuthFragment() {
    private var requestAdapter: RequestAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_profile_following, container, false)
    }

    /**
     *  Setup listeners and attach observers to
     *  observable [authUserVm] resources.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authUserVm.run {
            followRequests.observe(viewLifecycleOwner, Observer { updateRequestsFromViewModel(it) })
        }

        requestAdapter = RequestAdapter(requireContext(), onProfileClick, onAccept, onDecline)
        followingRecyclerView.apply {
            isNestedScrollingEnabled = false
            adapter = requestAdapter
        }

        swipeRefreshLayout.setOnRefreshListener { authUserVm.getIncomingFollowRequests() }

        authUserVm.getIncomingFollowRequests()
    }

    private val onProfileClick: (Pair<FollowRequestItem, UserItem>) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profile?userId=${it.second.id}"))
    }

    private val onAccept: (Pair<FollowRequestItem, UserItem>) -> Unit = {
        authUserVm.acceptRequest(it.first.requesterId)
    }

    private val onDecline: (Pair<FollowRequestItem, UserItem>) -> Unit = {
        authUserVm.declineRequest(it.first.requesterId)
    }

    /**
     *  Called when the observed incoming follow requests
     *  resource changes.
     *
     *  @param res The observed resource.
     */
    private fun updateRequestsFromViewModel(res: Resource<List<Pair<FollowRequestItem, UserItem>>>) {
        res.run {
            swipeRefreshLayout.run {
                when (state) {
                    ResourceState.LOADING -> startRefreshing()
                    ResourceState.SUCCESS -> {
                        stopRefreshing()
                        data?.let {
                            requestAdapter?.submitList(it)
                            requestAdapter?.notifyDataSetChanged()
                        }
                    }
                    ResourceState.ERROR -> {
                        stopRefreshing()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Clean up leaks?
        requestAdapter = null
        followingRecyclerView?.adapter = null
    }

    internal companion object {
        const val TAG = "AuthProfileFragment"
    }
}
