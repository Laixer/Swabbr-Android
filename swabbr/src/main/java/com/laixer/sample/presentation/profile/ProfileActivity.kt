package com.laixer.swabbr.presentation.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.laixer.navigation.features.SampleNavigation
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.ProfileItem
import com.laixer.swabbr.presentation.model.VlogItem
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.swipeRefreshLayout
import kotlinx.android.synthetic.main.include_user_info.*
import org.koin.androidx.viewmodel.ext.viewModel

class ProfileActivity : AppCompatActivity() {

    private val vm: ProfileViewModel by viewModel()
    private val userId by lazy { intent.getStringExtra(SampleNavigation.USER_ID_KEY) }
    private val snackBar by lazy {
        Snackbar.make(swipeRefreshLayout, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) { vm.getProfileVlogs(userId, refresh = true) }
    }
    private val adapter = ProfileVlogsAdapter()
    private lateinit var followStatus: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        followButton.setOnClickListener {
            when (followStatus) {
                "accepted" -> vm.unfollow(userId)
                "pending" -> vm.cancelFollowRequest(userId)
                else -> vm.sendFollowRequest(userId)
            }
        }

        injectFeature()

        if (savedInstanceState == null) {
            vm.getProfile(userId)
            vm.getProfileVlogs(userId)
            vm.getFollowStatus(userId)
        }

        profilevlogsRecyclerView.isNestedScrollingEnabled = false
        profilevlogsRecyclerView.adapter = adapter

        vm.profile.observe(this, Observer { updateProfile(it) })
        vm.profileVlogs.observe(this, Observer { updateProfileVlogs(it) })
        vm.followStatus.observe(this, Observer { updateFollowStatus(it) })
        swipeRefreshLayout.setOnRefreshListener {
            vm.getProfileVlogs(userId, refresh = true)
            vm.getFollowStatus(userId)
        }
    }

    private fun updateProfile(profileItem: ProfileItem?) {
        profileItem?.let {
            userAvatar.loadAvatar(it.id)
            userUsername.text = baseContext.getString(R.string.nickname, it.nickname)
            userName.text = baseContext.getString(R.string.full_name, it.firstName, it.lastName)
        }
    }

    private fun updateProfileVlogs(res: Resource<List<VlogItem>?>) {
        when (res.state) {
            ResourceState.LOADING -> swipeRefreshLayout.startRefreshing()
            ResourceState.SUCCESS -> swipeRefreshLayout.stopRefreshing()
            ResourceState.ERROR -> swipeRefreshLayout.stopRefreshing()
        }
        res.data?.let { adapter.submitList(it) }
        res.message?.let { snackBar.show() }
    }

    private fun updateFollowStatus(res: Resource<String?>) {
        when (res.state) {
            ResourceState.LOADING -> {
                followButton.isEnabled = false
                swipeRefreshLayout.startRefreshing()
            }
            ResourceState.SUCCESS -> {
                swipeRefreshLayout.stopRefreshing()
                this.followStatus = res.data!!
                followButton.text = when (followStatus) {
                    "pending" -> getString(R.string.requested)
                    "accepted" -> getString(R.string.following)
                    else -> getString(R.string.follow)
                }
                followButton.isEnabled = true
            }
            ResourceState.ERROR -> {
                swipeRefreshLayout.stopRefreshing()
                Toast.makeText(this, res.message, Toast.LENGTH_SHORT).show()
                followButton.isEnabled = true
            }
        }
    }
}
