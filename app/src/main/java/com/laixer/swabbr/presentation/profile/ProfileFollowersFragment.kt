package com.laixer.swabbr.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import com.laixer.swabbr.presentation.utils.todosortme.startRefreshing
import com.laixer.swabbr.presentation.utils.todosortme.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.onClickProfileWithRelation
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.auth.AuthFragment
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import com.laixer.swabbr.presentation.user.list.UserFollowRequestingAdapter
import com.laixer.swabbr.presentation.user.list.UserWithRelationAdapter
import kotlinx.android.synthetic.main.fragment_profile_followers.*
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
    private val profileVm: ProfileViewModel by viewModel()

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

        swipe_refresh_layout_profile_followers.setOnRefreshListener { getData(true) }

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
        if (authVm.getSelfIdOrNull() == userId) {
            profileVm.getFollowersAndIncomingRequesters(refresh)
        } else {
            profileVm.getFollowers(userId, refresh)
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
                    ResourceState.LOADING -> swipe_refresh_layout_profile_followers.startRefreshing()
                    ResourceState.SUCCESS -> {
                        swipe_refresh_layout_profile_followers.stopRefreshing()

                        data?.let {
                            userAdapter?.submitList(it)
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
