package com.laixer.swabbr.presentation.likeoverview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.presentation.utils.todosortme.startRefreshing
import com.laixer.swabbr.presentation.utils.todosortme.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.extensions.onClickProfileWithRelation
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.auth.AuthFragment
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import com.laixer.swabbr.presentation.user.list.UserFollowableAdapter
import com.laixer.swabbr.presentation.user.list.UserWithRelationAdapter
import com.laixer.swabbr.utils.resources.ResourceState
import kotlinx.android.synthetic.main.fragment_like_overview.*
import org.koin.androidx.viewmodel.ext.android.viewModel

// TODO Implement vlog like properties here? Or only for the diff callback thingy?
/**
 *  Fragment representing the vlog liking users overview tab.
 */
class LikeOverviewFragment : AuthFragment() {
    private val likeOverviewVm: LikeOverviewViewModel by viewModel()
    private lateinit var likingUserAdapter: UserWithRelationAdapter

    /**
     *  Sets up observers.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        likeOverviewVm.users.observe(viewLifecycleOwner, Observer { onVlogLikingUsersLoaded(it) })

        return inflater.inflate(R.layout.fragment_like_overview, container, false)
    }

    /**
     *  Sets up the UI and starts the data fetch.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        likingUserAdapter = UserFollowableAdapter(
            context = requireContext(),
            onClickProfile = onClickProfileWithRelation(),
            onClickFollow = onFollowClick
        )

        recycler_view_liking_users.apply {
            isNestedScrollingEnabled = false
            adapter = likingUserAdapter

            // TODO Add a listener to go to the next page if we scroll to the bottom?
        }

        // Swipe down to refresh the result set.
        swipe_refresh_layout_liking_users.setOnRefreshListener { likeOverviewVm.getLikingUserWrappers() }

        /** Start the data fetch. Response is handled by [onVlogLikingUsersLoaded] */
        likeOverviewVm.getLikingUserWrappers()
    }

    /**
     *  Callback for when we click a follow button.
     */
    private val onFollowClick: (UserWithRelationItem) -> Unit = {
        when (it.followRequestStatus) {
            FollowRequestStatus.PENDING -> likeOverviewVm.cancelFollowRequest(it.user.id)
            FollowRequestStatus.ACCEPTED -> likeOverviewVm.unfollow(it.user.id)
            FollowRequestStatus.DECLINED -> likeOverviewVm.follow(it.user.id)
            FollowRequestStatus.NONEXISTENT -> likeOverviewVm.follow(it.user.id)
        }
    }

    /**
     *  Called when the [likeOverviewVm] vlog liking users resource changes.
     */
    private fun onVlogLikingUsersLoaded(resource: Resource<List<UserWithRelationItem>>) = with(resource) {
        when (state) {
            ResourceState.LOADING -> {
                swipe_refresh_layout_liking_users.startRefreshing()
            }
            ResourceState.SUCCESS -> {
                swipe_refresh_layout_liking_users.stopRefreshing()

                data?.let {
                    likingUserAdapter.submitList(it)
                    likingUserAdapter.notifyDataSetChanged()
                }
            }
            ResourceState.ERROR -> {
                swipe_refresh_layout_liking_users.stopRefreshing()
                showMessage("Error loading vlog likes")
            }
        }
    }
}
