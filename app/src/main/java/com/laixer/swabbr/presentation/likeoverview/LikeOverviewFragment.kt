package com.laixer.swabbr.presentation.likeoverview

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.LikingUserWrapperItem
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import com.laixer.swabbr.presentation.user.list.UserWithRelationAdapter
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
        likeOverviewVm.likingUserWrappers.observe(viewLifecycleOwner, Observer { onVlogLikingUsersLoaded(it) })

        return inflater.inflate(R.layout.fragment_like_overview, container, false)
    }

    /**
     *  Sets up the UI and starts the data fetch.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        likingUserAdapter = UserWithRelationAdapter(
            context = requireContext(),
            onClickProfile = onProfileClick,
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
     *  Callback for when we click a user. This will take
     *  user to the profile of the clicked user.
     */
    private val onProfileClick: (UserWithRelationItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profile?userId=${it.user.id}"))
    }

    /**
     *  Callback for when we click a follow button.
     */
    private val onFollowClick: (UserWithRelationItem) -> Unit = {
            likeOverviewVm.follow(it.user.id)
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
                Toast.makeText(requireContext(), "Error loading vlog likes - ${resource.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
