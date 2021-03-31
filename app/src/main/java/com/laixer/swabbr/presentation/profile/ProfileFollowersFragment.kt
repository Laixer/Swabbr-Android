package com.laixer.swabbr.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.onClickProfileWithRelation
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.auth.AuthFragment
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import com.laixer.swabbr.presentation.user.list.UserFollowRequestingAdapter
import com.laixer.swabbr.presentation.user.list.UserWithRelationAdapter
import com.laixer.swabbr.presentation.utils.todosortme.startRefreshing
import com.laixer.swabbr.presentation.utils.todosortme.stopRefreshing
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import kotlinx.android.synthetic.main.fragment_profile_followers.*
import java.util.*

/**
 *  Fragment displaying all users that are following the displayed
 *  user. Note that for the current user this also shows incoming
 *  follow requests.
 *
 *  @param userId The user id of the profile we are looking at.
 *  @param profileVm Single profile vm instance from [ProfileFragment].
 */
class ProfileFollowersFragment(
    private val userId: UUID,
    private val profileVm: ProfileViewModel
) : AuthFragment() {

    private var userAdapter: UserWithRelationAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_followers, container, false)
    }

    /**
     *  Setup listeners and attach observers to the [authVm] resources.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup and store the adapter.
        userAdapter = UserFollowRequestingAdapter(
            context = requireContext(),
            onClickProfile = onClickProfileWithRelation(),
            onClickAccept = onAccept,
            onClickDecline = onDecline
        )
        recycler_view_profile_followers.isNestedScrollingEnabled = false
        recycler_view_profile_followers.adapter = userAdapter

        swipe_refresh_layout_profile_followers.setOnRefreshListener { refreshData() }

        profileVm.followersAndFollowRequestingUsers.observe(viewLifecycleOwner, Observer { onFollowersUpdated(it) })
    }

    /**
     *  Only performs data refreshes. Note that this does not
     *  follow the [getData] structure as our parent fragment
     *  manages the initial data get operation.
     */
    private fun refreshData() {
        if (authVm.getSelfIdOrNull() == userId) {
            profileVm.getFollowersAndIncomingRequesters(true)
        } else {
            profileVm.getFollowers(userId, true)
        }
    }

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
            when (state) {
                ResourceState.LOADING -> {
                    swipe_refresh_layout_profile_followers.startRefreshing()
                }
                ResourceState.SUCCESS -> {
                    swipe_refresh_layout_profile_followers.stopRefreshing()

                    data?.let {
                        userAdapter?.submitList(it)
                        userAdapter?.notifyDataSetChanged()
                    }
                }
                ResourceState.ERROR -> {
                    swipe_refresh_layout_profile_followers.stopRefreshing()

                    showMessage("Could not get followers")
                }
            }
        }
    }

    /**
     *  Dispose adapter to prevent memory leak.
     */
    override fun onDestroyView() {
        super.onDestroyView()

        userAdapter = null
    }
}
