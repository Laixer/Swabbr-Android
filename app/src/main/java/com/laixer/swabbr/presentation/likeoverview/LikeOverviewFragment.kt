package com.laixer.swabbr.presentation.likeoverview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.onClickProfileWithRelation
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.auth.AuthFragment
import com.laixer.swabbr.presentation.model.LikingUserWrapperItem
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import com.laixer.swabbr.presentation.user.list.UserWithRelationAdapter
import com.laixer.swabbr.presentation.user.list.UserWithVlogAdapter
import com.laixer.swabbr.presentation.utils.todosortme.startRefreshing
import com.laixer.swabbr.presentation.utils.todosortme.stopRefreshing
import com.laixer.swabbr.utils.resources.Resource
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

        likingUserAdapter = UserWithVlogAdapter(
            context = requireContext(),
            onClickProfile = onClickProfileWithRelation(),
            onClickVlog = onClickVlog,
            currentUserId = authVm.getSelfIdOrNull()
        )

        recycler_view_liking_users.apply {
            isNestedScrollingEnabled = false
            adapter = likingUserAdapter

            // TODO Add a listener to go to the next page if we scroll to the bottom?
        }

        // Swipe down to refresh the result set.
        swipe_refresh_layout_liking_users.setOnRefreshListener { likeOverviewVm.getLikingUserWrappers() }
    }

    override fun getData(refresh: Boolean) {
        likeOverviewVm.getLikingUserWrappers()
    }

    /**
     *  Callback for when we click a follow button.
     */
    private val onClickVlog: (UserWithRelationItem) -> Unit = {
        try {
            val cast = it as LikingUserWrapperItem
            val action = LikeOverviewFragmentDirections.actionLikeOverviewFragmentToWatchUserVlogsFragment(
                initialVlogId = cast.vlogLikeItem.vlogId.toString(),
                userId = authVm.getSelfIdOrNull()!!.toString() // TODO Ugly - but we always go to our own profile.
            )
            findNavController().navigate(action)
        } catch (e: Exception) {
            Log.e(TAG, "Could not navigate to vlog", e)
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
