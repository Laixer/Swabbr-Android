package com.laixer.swabbr.presentation.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.extensions.reduceDragSensitivity
import com.laixer.swabbr.extensions.showMessage
import com.laixer.swabbr.presentation.auth.AuthFragment
import com.laixer.swabbr.presentation.model.FollowRequestItem
import com.laixer.swabbr.presentation.model.UserWithStatsItem
import com.laixer.swabbr.utils.formatNumber
import com.laixer.swabbr.utils.loadAvatarFromUser
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.include_profile_top_section.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.properties.Delegates

/**
 *  Fragment for displaying generic user profile information.
 *  This fragment contains tabs for more specific user details
 *  and information display. If this profile displays the current
 *  user, additional information is displayed.
 *
 *  This fragment controls the initial data get operations. The
 *  individual tabs will not call these methods and will only
 *  observe the initial behaviour. Refreshing behaviour can be
 *  controlled by individual tabs.
 */
class ProfileFragment : AuthFragment() {
    private val args: ProfileFragmentArgs by navArgs()
    private val profileVm: ProfileViewModel by viewModel()

    // TODO Is this the best solution? Might be - probably not though.
    /**
     *  The id of the profile that we are looking at. If no user id has
     *  been specified, this is assigned as the current users id.
     */
    private val userId: UUID by lazy {
        if (args.userId == "self") {
            getSelfId()
        } else {
            UUID.fromString(args.userId)
        }
    }

    /**
     *  Indicates if we are looking at the currently authenticated user.
     *  Only call this when we are authenticated, else we can't guarantee
     *  that this boolean flag is correct.
     */
    private val isSelf by lazy {
        authVm.getSelfIdOrNull() == userId
    }

    /**
     *  Binds update functions to observable resources in the [authVm].
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        profileVm.user.observe(viewLifecycleOwner, Observer { onUserUpdated(it) })
        // TODO Conditional observe? Right now we simply never call the resource if we are self.
        profileVm.followRequestAsCurrentUser.observe(viewLifecycleOwner, Observer { onFollowRequestUpdated(it) })

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    /**
     *  Setup for the tabs and viewpager displaying the tabs.
     */
    @SuppressLint("WrongConstant") // For offscreenPageLimit
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO Fix
        // Refresh layout
        // swipe_refresh_layout_profile_top.setOnRefreshListener { getData(true) }

        // Reduce swiping sensitivity for tabs.
        viewpager_user_profile.reduceDragSensitivity()

        // Set the follow button
        button_profile_follow.isEnabled = !isSelf
        button_profile_follow.isVisible = !isSelf
        button_profile_follow.setOnClickListener { onClickFollowButton() }

        /** Setup the tab layout based on [isSelf]. */
        if (isSelf) {
            viewpager_user_profile.adapter = ProfileTabSelfAdapter(this, userId, profileVm)
            viewpager_user_profile.offscreenPageLimit = ProfileTabSelfAdapter.ITEM_COUNT

            TabLayoutMediator(tab_layout_user_profile, viewpager_user_profile) { tab, position ->
                tab.text = when (position) {
                    0 -> requireContext().getString(R.string.tab_vlogs)
                    1 -> requireContext().getString(R.string.tab_profile)
                    2 -> requireContext().getString(R.string.tab_following)
                    3 -> requireContext().getString(R.string.tab_followers)
                    else -> "UNDEFINED"
                }
            }.attach()
        } else {
            viewpager_user_profile.adapter = ProfileTabAdapter(this, userId, profileVm)
            viewpager_user_profile.offscreenPageLimit = ProfileTabAdapter.ITEM_COUNT

            TabLayoutMediator(tab_layout_user_profile, viewpager_user_profile) { tab, position ->
                tab.text = when (position) {
                    0 -> requireContext().getString(R.string.tab_vlogs)
                    1 -> requireContext().getString(R.string.tab_following)
                    2 -> requireContext().getString(R.string.tab_followers)
                    else -> "UNDEFINED"
                }
            }.attach()
        }
    }

    /**
     *  Gets the user from the view model.
     *
     *  @param refresh Force a data refresh.
     */
    override fun getData(refresh: Boolean) {
        profileVm.getUser(userId, refresh)
        profileVm.getFollowing(userId, refresh)
        profileVm.getVlogsByUser(userId, refresh)

        // Conditional for incoming follow requests or not
        if (isSelf) {
            // TODO Is this the way to go? Might be though.
            // Always get new incoming follow requests
            profileVm.getFollowersAndIncomingRequesters(refresh = true)
        } else {
            profileVm.getFollowers(userId, refresh)
        }

        if (isSelf) {
            // TODO Bad design. Make the call itself get the URI maybe?
            // Always set this to true since we always need a valid profile image upload URI.
            profileVm.getSelfComplete(refresh = true)
        }

        // If we are looking at another profile, get the follow request status too.
        if (!isSelf) {
            profileVm.getFollowRequestAsCurrentUser(userId)
        }
    }

    /**
     *  Conditional behavior when we click the follow button.
     */
    private fun onClickFollowButton() {
        // This shouldn't be enabled when we are looking at our own profile.
        if (isSelf) {
            return
        }

        // This should't be enabled when we don't know the current follow status yet.
        if (profileVm.followRequestAsCurrentUser.value == null || profileVm.followRequestAsCurrentUser.value!!.data == null) {
            return
        }

        when (profileVm.followRequestAsCurrentUser.value!!.data!!.requestStatus) {
            FollowRequestStatus.PENDING -> profileVm.cancelFollowRequest(userId)
            FollowRequestStatus.ACCEPTED -> profileVm.unfollow(userId)
            FollowRequestStatus.DECLINED -> profileVm.sendFollowRequest(userId)
            FollowRequestStatus.NONEXISTENT -> profileVm.sendFollowRequest(userId)
        }
    }

    /**
     *  This function is attached to the observable stats object
     *  in the [authVm]. Whenever the current stats object in
     *  [authVm] changes, this function gets called.
     *
     *  @param res The user resource.
     */
    private fun onUserUpdated(res: Resource<UserWithStatsItem>) {
        when (res.state) {
            ResourceState.LOADING -> {
            }
            ResourceState.SUCCESS -> {
                res.data?.let { user ->
                    // User information
                    user_profile_profile_image.loadAvatarFromUser(user)
                    user_profile_nickname.text = requireContext().getString(R.string.nickname, user.nickname)

                    // User stats
                    user_profile_followers_count.text = requireContext().formatNumber(user.totalFollowers)
                    user_profile_following_count.text = requireContext().formatNumber(user.totalFollowing)
                    user_profile_vlog_count.text = requireContext().formatNumber(user.totalVlogs)
                    user_profile_views.text = requireContext().formatNumber(user.totalViews)
                    user_profile_likes_received.text = requireContext().formatNumber(user.totalLikesReceived)
                    user_profile_reactions_received.text =
                        requireContext().formatNumber(user.totalReactionsReceived)
                }
            }
            ResourceState.ERROR -> {
                showMessage(res.message ?: "Error loading profile information")
            }
        }
    }

    /**
     *  Called when the follow request between the current user and the
     *  displayed user is updated a mutable resource.
     */
    private fun onFollowRequestUpdated(res: Resource<FollowRequestItem>) {
        when (res.state) {
            ResourceState.LOADING -> {
                button_profile_follow.isEnabled = false
            }
            ResourceState.SUCCESS -> {
                button_profile_follow.isEnabled = true

                res.data?.let { item ->
                    button_profile_follow.text = when (item.requestStatus) {
                        FollowRequestStatus.PENDING -> requireContext().getString(R.string.follow_request_requested)
                        FollowRequestStatus.ACCEPTED -> requireContext().getString(R.string.follow_request_accepted)
                        FollowRequestStatus.DECLINED -> requireContext().getString(R.string.follow_request_follow)
                        FollowRequestStatus.NONEXISTENT -> requireContext().getString(R.string.follow_request_follow)
                    }
                }
            }
            ResourceState.ERROR -> {
                button_profile_follow.isEnabled = true

                showMessage(res.message ?: "Error loading profile follow request status")
            }
        }
    }

    /**
     *  Dispose adapter to prevent memory leak.
     */
    override fun onDestroyView() {
        super.onDestroyView()

        viewpager_user_profile?.let {
            it.adapter = null
        }
    }

    internal companion object {
        private val TAG = ProfileFragment::class.java.simpleName
    }
}
