package com.laixer.swabbr.presentation.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import com.laixer.swabbr.presentation.user.list.UserWithRelationAdapter
import kotlinx.android.synthetic.main.fragment_profile_followers.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

/**
 *  Fragment displaying all users that are following the displayed
 *  user. Note that for the current user this also shows incoming
 *  follow requests.
 *
 *  @param userId The user id of the profile we are looking at.
 *
 */
class ProfileFollowersFragment(private val userId: UUID) : AuthFragment() {
    private val profileVm: ProfileViewModel by sharedViewModel()

    private lateinit var adapter: UserWithRelationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_followers, container, false)
    }

    /**
     *  Setup listeners and attach observers to the [authUserVm] resources.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup and store the adapter.
        adapter = UserWithRelationAdapter(
            context = requireContext(),
            onClickProfile = onProfileClick,
            onClickFollow = todoFixThisCallback
        )
        recycler_view_profile_followers.isNestedScrollingEnabled = false
        recycler_view_profile_followers.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener { getData(true) }

        profileVm.followersAndFollowRequestingUsers.observe(viewLifecycleOwner, Observer { onFollowersUpdated(it) })

        getData()
    }

    /**
     *  Gets data from the [profileVm] based on if the profile
     *  belongs to the current user or not.
     *
     *  @param refresh Force a data refresh.
     */
    private fun getData(refresh: Boolean = false) {
        if (authUserVm.getSelfId() == userId) {
            profileVm.getFollowersAndIncomingRequesters(refresh)
        } else {
            profileVm.getFollowers(userId, refresh)
        }
    }

    // TODO Duplicate, centralize or something.
    /**
     *  Callback for when we click on a profile.
     */
    private val onProfileClick: (UserWithRelationItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profile?userId=${it.user.id}"))
    }

    // TODO Fix
    private val todoFixThisCallback: (UserWithRelationItem) -> Unit = {}

    /**
     *  Callback for when we accept a follow request.
     */
    private val onAccept: (UserWithRelationItem) -> Unit = {
        profileVm.acceptRequest(it.user.id)
    }

    /**
     *  Callback for when we decline a follow request.
     */
    private val onDecline: (UserWithRelationItem) -> Unit = {
        profileVm.declineRequest(it.user.id)
    }

    /**
     *  Called when the observed incoming follow requests
     *  resource changes.
     *
     *  @param res The observed resource.
     */
    private fun onFollowersUpdated(res: Resource<List<UserWithRelationItem>>) {
        res.run {
            swipeRefreshLayout.run {
                when (state) {
                    ResourceState.LOADING -> startRefreshing()
                    ResourceState.SUCCESS -> {
                        stopRefreshing()

                        data?.let {
                            adapter.submitList(it)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    ResourceState.ERROR -> {
                        stopRefreshing()
                    }
                }
            }
        }
    }
}
