package com.laixer.swabbr.presentation.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.laixer.navigation.features.SwabbrNavigation
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.FollowRequestItem
import com.laixer.swabbr.presentation.model.ProfileItem
import com.laixer.swabbr.presentation.model.VlogItem
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.swipeRefreshLayout
import kotlinx.android.synthetic.main.include_user_info.*
import org.koin.androidx.viewmodel.ext.viewModel

class ProfileActivity : AppCompatActivity() {

    private val vm: ProfileViewModel by viewModel()
    private val userId by lazy { intent.getStringExtra(SwabbrNavigation.USER_ID_KEY) }
    private val snackBar by lazy {
        Snackbar.make(swipeRefreshLayout, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) { refresh() }
    }
    private val adapter = ProfileVlogsAdapter()
    private var followStatus = -1
    private var followRequestId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(findViewById(R.id.toolbar))

        followButton.setOnClickListener {
            when (followStatus) {
                1 -> vm.unfollow(userId)
                0 -> vm.cancelFollowRequest(followRequestId)
                else -> vm.sendFollowRequest(userId)
            }
        }

        injectFeature()

        if (savedInstanceState == null) {
            vm.getProfile(userId)
            vm.getProfileVlogs(userId)
            vm.getFollowRequest(userId)
        }

        profilevlogsRecyclerView.isNestedScrollingEnabled = false
        profilevlogsRecyclerView.adapter = adapter

        vm.profile.observe(this, Observer { updateProfile(it) })
        vm.profileVlogs.observe(this, Observer { updateProfileVlogs(it) })
        vm.followRequest.observe(this, Observer { updateFollowRequest(it) })
        swipeRefreshLayout.setOnRefreshListener {
            vm.getProfile(userId)
            vm.getProfileVlogs(userId, refresh = true)
            vm.getFollowRequest(userId)
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

    private fun updateFollowRequest(res: Resource<FollowRequestItem?>) {
        when (res.state) {
            ResourceState.LOADING -> {
                followButton.isEnabled = false
                swipeRefreshLayout.startRefreshing()
            }
            ResourceState.SUCCESS -> {
                swipeRefreshLayout.stopRefreshing()
                this.followStatus = res.data!!.status
                this.followRequestId = res.data!!.followRequestId
                followButton.text = when (followStatus) {
                    0 -> getString(R.string.requested)
                    1 -> getString(R.string.following)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings -> {
            startActivity(SwabbrNavigation.settings())
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun refresh() {
        vm.getProfile(userId)
        vm.getProfileVlogs(userId)
        vm.getFollowRequest(userId)
    }
}
