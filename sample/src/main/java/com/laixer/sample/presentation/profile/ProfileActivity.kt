package com.laixer.sample.presentation.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.laixer.navigation.features.SampleNavigation
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.startRefreshing
import com.laixer.presentation.stopRefreshing
import com.laixer.sample.R
import com.laixer.sample.injectFeature
import com.laixer.sample.presentation.loadAvatar
import com.laixer.sample.presentation.model.ProfileItem
import com.laixer.sample.presentation.model.VlogItem
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        followButton.setOnClickListener{
            // TODO: METHODE TOEVOEGEN AAN BUTTON
        }

        injectFeature()

        if (savedInstanceState == null) {
            vm.getProfile(userId)
            vm.getProfileVlogs(userId)
            vm.getFollowStatus(userId) // TODO: Target ID
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

    private fun updateProfileVlogs(resource: Resource<List<VlogItem>>?) {
        resource?.let { res ->
            when (res.state) {
                ResourceState.LOADING -> swipeRefreshLayout.startRefreshing()
                ResourceState.SUCCESS -> swipeRefreshLayout.stopRefreshing()
                ResourceState.ERROR -> swipeRefreshLayout.stopRefreshing()
            }
            res.data?.let { adapter.submitList(it) }
            res.message?.let { snackBar.show() }
        }
    }

    private fun updateFollowStatus(followStatus: String?) {
        followButton.text = followStatus
    }
}
