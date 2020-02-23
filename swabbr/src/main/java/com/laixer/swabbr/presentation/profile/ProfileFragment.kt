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
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.ProfileItem
import com.laixer.swabbr.presentation.model.VlogItem
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.include_user_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private val args by navArgs<ProfileFragmentArgs>()
    private val vm: ProfileViewModel by viewModel()
    private val userId by lazy { args.userId ?: "0" }
    private val snackBar by lazy {
        Snackbar.make(swipeRefreshLayout, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) { vm.getProfileVlogs(userId, refresh = true) }
    }
    private var profileVlogsAdapter: ProfileVlogsAdapter? = null
    private var followStatus: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileVlogsAdapter = ProfileVlogsAdapter(context!!, onClick)
        followButton.setOnClickListener {
            with(vm) {
                when (this@ProfileFragment.followStatus) {
                    "accepted" -> unfollow(userId)
                    "pending" -> cancelFollowRequest(userId)
                    else -> sendFollowRequest(userId)
                }
            }
        }

        injectFeature()

        if (savedInstanceState == null) {
            vm.run {
                with(userId) {
                    getProfile(this)
                    getProfileVlogs(this)
                    getFollowStatus(this)
                }
            }
        }

        profilevlogsRecyclerView.run {
            isNestedScrollingEnabled = false
            adapter = profileVlogsAdapter
        }

        vm.run {
            profile.observe(viewLifecycleOwner, Observer { updateProfile(it) })
            profileVlogs.observe(viewLifecycleOwner, Observer { updateProfileVlogs(it) })
            followStatus.observe(viewLifecycleOwner, Observer { updateFollowStatus(it) })
            swipeRefreshLayout.setOnRefreshListener {
                getProfileVlogs(userId, refresh = true)
                getFollowStatus(userId)
            }
        }
    }

    private val onClick: (VlogItem) -> Unit =
        { findNavController().navigate(ProfileFragmentDirections.actionViewVlog(arrayOf(it.vlogId))) }

    private fun updateProfile(profileItem: ProfileItem?) {
        profileItem?.let {
            userAvatar.loadAvatar(it.id)
            userUsername.text = context!!.getString(R.string.nickname, it.nickname)
            userName.text =
                context!!.getString(R.string.full_name, it.firstName, it.lastName)
        }
    }

    private fun updateProfileVlogs(res: Resource<List<VlogItem>?>) =
        res.run {
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

    private fun updateFollowStatus(res: Resource<String?>) = res.run {
        swipeRefreshLayout.run {
            when (state) {
                ResourceState.LOADING -> {
                    followButton.isEnabled = false
                    startRefreshing()
                }
                ResourceState.SUCCESS -> {
                    stopRefreshing()
                    followStatus = data
                    followButton.run {
                        text = when (followStatus) {
                            "pending" -> getString(R.string.requested)
                            "accepted" -> getString(R.string.following)
                            else -> getString(R.string.follow)
                        }
                        isEnabled = true
                    }
                }
                ResourceState.ERROR -> {
                    stopRefreshing()
                    followButton.text = getString(R.string.requested)
//                    Toast.makeText(activity, res.message, Toast.LENGTH_SHORT).show()
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
