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
import com.laixer.swabbr.domain.model.FollowStatus
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.FollowStatusItem
import com.laixer.swabbr.presentation.model.LikeItem
import com.laixer.swabbr.presentation.model.LikeListItem
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.UserVlogItem
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.include_user_info.*
import kotlinx.android.synthetic.main.reactions_sheet.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.UUID

class ProfileFragment : AuthFragment() {

    private val args by navArgs<ProfileFragmentArgs>()
    private val profileVm: ProfileViewModel by sharedViewModel()
    private val userId by lazy { UUID.fromString(args.userId) }
    private val snackBar by lazy {
        Snackbar.make(swipeRefreshLayout, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) { profileVm.getProfileVlogs(userId, refresh = true) }
    }
    private var profileVlogsAdapter: ProfileVlogsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            profileVm.run {
                with(userId) {
                    getProfile(this, refresh = true)
                    getProfileVlogs(this, refresh = true)
                    getFollowStatus(this)
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

        profileVlogsAdapter = ProfileVlogsAdapter(onClick)

        followButton.setOnClickListener {
            with(profileVm) {
                when (profileVm.followStatus.value?.data?.status) {
                    FollowStatus.FOLLOWING -> unfollow(userId)
                    FollowStatus.PENDING -> cancelFollowRequest(userId)
                    FollowStatus.NOT_FOLLOWING -> sendFollowRequest(userId)
                    FollowStatus.DECLINED -> sendFollowRequest(userId)
                    else -> {
                        getFollowStatus(userId)
                    }
                }
            }
        }

        if (savedInstanceState == null) {
            profileVm.run {
                with(userId) {
                    getProfile(this, refresh = false)
                    getProfileVlogs(this, refresh = false)
                    getFollowStatus(this)
                }
            }
        }

        profileVlogsRecyclerView.run {
            isNestedScrollingEnabled = false
            adapter = profileVlogsAdapter
        }

        profileVm.run {
            profile.observe(viewLifecycleOwner, Observer { updateProfile(it) })
            profileVlogs.observe(viewLifecycleOwner, Observer { updateProfileVlogs(it) })
            followStatus.observe(viewLifecycleOwner, Observer { updateFollowStatus(it) })
            swipeRefreshLayout.setOnRefreshListener {
                getProfileVlogs(userId, refresh = true)
                getFollowStatus(userId)
            }
        }
    }

    private val onClick: (UserVlogItem) -> Unit = {
        findNavController().navigate(Uri.parse("https://swabbr.com/user/${it.userId}/vlog/${it.vlogId}"))
    }

    private fun updateProfile(res: Resource<UserItem>) = res.run {
        data?.let { item ->
            user_avatar.loadAvatar(item.profileImage, item.id)
            user_nickname.text = requireContext().getString(R.string.nickname, item.nickname)
            item.firstName?.let {
                user_username.text = requireContext().getString(R.string.full_name, it, item.lastName)
                user_username.visibility = View.VISIBLE
            }
        }
    }

    private fun updateProfileVlogs(res: Resource<List<UserVlogItem>?>) = res.run {
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

    private fun updateFollowStatus(res: Resource<FollowStatusItem>) = res.run {
        swipeRefreshLayout.run {
            when (state) {
                ResourceState.LOADING -> {
                    followButton.isEnabled = false
                    startRefreshing()
                }
                ResourceState.SUCCESS -> {
                    stopRefreshing()
                    followButton.run {
                        text = when (data?.status) {
                            FollowStatus.PENDING -> getString(R.string.requested)
                            FollowStatus.FOLLOWING -> getString(R.string.following)
                            FollowStatus.NOT_FOLLOWING -> getString(R.string.follow)
                            FollowStatus.DECLINED -> getString(R.string.follow)
                            else -> getString(R.string.followStatusError)
                        }
                        isEnabled = data?.status?.let { true } ?: false
                    }
                }
                ResourceState.ERROR -> {
                    stopRefreshing()
                    followButton.text = getString(R.string.followStatusError)
                    // Toast.makeText(activity, res.message, Toast.LENGTH_SHORT).show()
                    followButton.isEnabled = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileVlogsAdapter = null
    }
}
