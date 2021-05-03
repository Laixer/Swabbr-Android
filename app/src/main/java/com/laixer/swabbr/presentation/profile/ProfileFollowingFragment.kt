package com.laixer.swabbr.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.onClickProfile
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.auth.AuthFragment
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.user.list.UserAdapter
import com.laixer.swabbr.presentation.utils.todosortme.startRefreshing
import com.laixer.swabbr.presentation.utils.todosortme.stopRefreshing
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import kotlinx.android.synthetic.main.fragment_profile_following.*
import java.util.*

/**
 *  Fragment displaying all users that the displayed user is following himself.
 *
 *  @param userId The user id of the profile we are looking at.
 *  @param profileVm Single profile vm instance from [ProfileFragment].
 */
class ProfileFollowingFragment(
    private val userId: UUID,
    private val profileVm: ProfileViewModel
) : AuthFragment() {

    private var userAdapter: UserAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_following, container, false)
    }

    /**
     *  Setup UI, attach observers to observable [profileVm] vars.
     *  This will trigger [refreshData] to display up to date
     *  information about who the current user is following.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAdapter = UserAdapter(requireContext(), onClickProfile())
        recycler_view_profile_following.apply {
            isNestedScrollingEnabled = false
            adapter = userAdapter
        }

        swipe_refresh_layout_profile_following.setOnRefreshListener { refreshData() }

        profileVm.followingUsers.observe(viewLifecycleOwner, Observer { onFollowingUsersUpdated(it) })
    }

    /**
     *  Triggers a get for the following users.
     */
    private fun refreshData() = profileVm.getFollowing(userId, true)

    /**
     *  Called when the observed resource of [UserItem]s changes.
     *
     *  @param resource The observed resource.
     */
    private fun onFollowingUsersUpdated(resource: Resource<List<UserItem>>) {
        resource.run {
            when (state) {
                ResourceState.LOADING -> {
                    swipe_refresh_layout_profile_following.startRefreshing()
                }
                ResourceState.SUCCESS -> {
                    swipe_refresh_layout_profile_following.stopRefreshing()

                    data?.let {
                        userAdapter?.submitList(it)
                    }
                }
                ResourceState.ERROR -> {
                    swipe_refresh_layout_profile_following.stopRefreshing()

                    showMessage("Could not get following users")
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

    internal companion object {
        const val TAG = "ProfileFollowingFragment"
    }
}
