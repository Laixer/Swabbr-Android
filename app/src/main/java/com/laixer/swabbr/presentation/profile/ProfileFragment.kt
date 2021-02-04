package com.laixer.swabbr.presentation.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.FollowRequestItem
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

/**
 *  Fragment representing a profile for any user other than
 *  the currently logged in user.
 */
class ProfileFragment : AuthFragment() {

    private val args by navArgs<ProfileFragmentArgs>()
    private val profileVm: ProfileViewModel by sharedViewModel()
    private val receiverId by lazy { UUID.fromString(args.userId) }
    private val snackBar by lazy {
        Snackbar.make(swipeRefreshLayout, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) { profileVm.getProfileVlogs(receiverId, refresh = true) }
    }
    private var profileVlogsAdapter: ProfileVlogsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            profileVm.run {
                with(receiverId) {
                    getProfile(this, refresh = true)
                    getProfileVlogs(this, refresh = true)
                    getFollowRequest(this)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectFeature()

        profileVlogsAdapter = ProfileVlogsAdapter(requireContext(), profileVm, authUserVm, onClick)

        profileVlogsRecyclerView.run {
            isNestedScrollingEnabled = false
            adapter = profileVlogsAdapter // MAKE SURE THIS HAPPENS BEFORE ADAPTER INSTANTIATION
        }

        profileVm.run {
            profile.observe(viewLifecycleOwner, Observer { updateProfile(it) })
            profileVlogs.observe(viewLifecycleOwner, Observer { updateProfileVlogs(it) })
            followStatus.observe(viewLifecycleOwner, Observer { updateFollowStatus(it) })

            with(receiverId) {
                getProfile(this, refresh = false)
                getProfileVlogs(this, refresh = false)
                getFollowRequest(this)
            }
            swipeRefreshLayout.setOnRefreshListener {
                getProfileVlogs(receiverId, refresh = true)
                getFollowRequest(receiverId)
            }

            profile_follow_button.setOnClickListener {
                when (followStatus.value?.data?.requestStatus) {
                    FollowRequestStatus.ACCEPTED -> unfollow(receiverId)
                    FollowRequestStatus.PENDING -> cancelFollowRequest(receiverId)
                    FollowRequestStatus.DECLINED -> sendFollowRequest(receiverId)
                    FollowRequestStatus.NONEXISTENT -> sendFollowRequest(receiverId)
                    else -> {
                        getFollowRequest(receiverId)
                    }
                }
            }

        }
    }


    /**
     *  Click handler for when we click on a vlog.
     */
    private val onClick: (VlogWrapperItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/profileWatchVlog?userId=${it.user.id}&vlogId=${it.vlog.id}"))
    }

    private fun updateProfile(res: Resource<UserItem>) = res.run {
        // TODO Repair
//        data?.let { item ->
//            user_avatar.loadAvatar(item.profileImage, item.id)
//            user_nickname.text = requireContext().getString(R.string.nickname, item.nickname)
//            item.firstName?.let {
//                user_username.text = requireContext().getString(R.string.full_name, it, item.lastName)
//                user_username.visibility = View.VISIBLE
//            }
//        }
    }

    /**
     *  Called when we wish to update the vlogs for a profile.
     */
    private fun updateProfileVlogs(res: Resource<List<VlogWrapperItem>>) = res.run {
        with(swipeRefreshLayout) {
            when (state) {
                ResourceState.LOADING -> startRefreshing()
                ResourceState.SUCCESS -> stopRefreshing()
                ResourceState.ERROR -> stopRefreshing()
            }
        }

        data?.let {
            no_vlogs_text.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            profileVlogsRecyclerView.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            profileVlogsAdapter?.submitList(it)
        }
        message?.let { snackBar.show() }
    }

    /**
     *  Called when we wish to update the (displayed)
     *  status of a follow request.
     */
    private fun updateFollowStatus(res: Resource<FollowRequestItem>) = res.run {
        swipeRefreshLayout.run {
            when (state) {
                ResourceState.LOADING -> {
                    profile_follow_button.isEnabled = false
                    startRefreshing()
                }
                ResourceState.SUCCESS -> {
                    stopRefreshing()
                    profile_follow_button.run {
                        text = when (data?.requestStatus) {
                            FollowRequestStatus.PENDING -> getString(R.string.requested)
                            FollowRequestStatus.ACCEPTED -> getString(R.string.following)
                            FollowRequestStatus.DECLINED -> getString(R.string.follow)
                            FollowRequestStatus.NONEXISTENT -> getString(R.string.follow)
                            else -> getString(R.string.follow)
                        }
                        isEnabled = data?.requestStatus?.let { true } ?: false
                    }
                }
                ResourceState.ERROR -> {
                    stopRefreshing()
                    profile_follow_button.text = getString(R.string.followStatusError)
                    // Toast.makeText(activity, res.message, Toast.LENGTH_SHORT).show()
                    profile_follow_button.isEnabled = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileVlogsAdapter = null
    }
}
