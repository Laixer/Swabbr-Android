package com.laixer.swabbr.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.FollowRequestItem
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.VlogItem
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.include_user_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

class ProfileFragment : Fragment() {

    private val args by navArgs<ProfileFragmentArgs>()
    private val profileVm: ProfileViewModel by viewModel()

    private val userId by lazy { UUID.fromString(args.userId) }
    private val snackBar by lazy {
        Snackbar.make(swipeRefreshLayout, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) { profileVm.getProfileVlogs(userId, refresh = true) }
    }
    private var profileVlogsAdapter: ProfileVlogsAdapter? = null
    private var followStatus: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectFeature()

        profileVlogsAdapter = ProfileVlogsAdapter(requireContext(), onClick)

        followButton.setOnClickListener {
            with(profileVm) {
                when (this@ProfileFragment.followStatus) {
                    "accepted" -> unfollow(userId)
                    "pending" -> cancelFollowRequest(userId)
                    else -> sendFollowRequest(userId)
                }
            }
        }

        if (savedInstanceState == null) {
            profileVm.run {
                with(userId) {
                    getProfile(this)
                    getProfileVlogs(this)
                    getFollowRequest(this)
                }
            }
        }

        profilevlogsRecyclerView.run {
            isNestedScrollingEnabled = false
            adapter = profileVlogsAdapter
        }

        profileVm.run {
            profile.observe(viewLifecycleOwner, Observer { updateProfile(it) })
            profileVlogs.observe(viewLifecycleOwner, Observer { updateProfileVlogs(it) })
            followRequest.observe(viewLifecycleOwner, Observer { updateFollowRequest(it) })
            swipeRefreshLayout.setOnRefreshListener {
                getProfileVlogs(userId, refresh = true)
                getFollowRequest(userId)
            }
        }
    }

    private val onClick: (VlogItem) -> Unit = {
        findNavController().navigate(
            ProfileFragmentDirections.actionViewVlog(
                arrayOf(it.id.toString())
            )
        )
    }

    private fun updateProfile(userItem: UserItem?) {
        userItem?.let {
            userAvatar.loadAvatar(it.profileImageUrl)
            userUsername.text = requireContext().getString(R.string.nickname, it.nickname)
            userName.text = requireContext().getString(R.string.full_name, it.firstName, it.lastName)
        }
    }

    private fun updateProfileVlogs(res: Resource<List<VlogItem>?>) = res.run {
        with(swipeRefreshLayout) {
            when (state) {
                ResourceState.LOADING -> startRefreshing()
                ResourceState.SUCCESS -> stopRefreshing()
                ResourceState.ERROR -> stopRefreshing()
            }
        }

        data?.let {
            no_vlogs_text.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            profileVlogsAdapter?.submitList(it)
        }
        message?.let { snackBar.show() }
    }

    private fun updateFollowRequest(res: Resource<FollowRequestItem>) = res.run {
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
                            else -> getString(R.string.followStatusError)
                        }
                        isEnabled = true
                    }
                }
                ResourceState.ERROR -> {
                    stopRefreshing()
                    followButton.text = getString(R.string.requested)
                    // Toast.makeText(activity, res.message, Toast.LENGTH_SHORT).show()
                    followButton.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileVlogsAdapter = null
    }
}
