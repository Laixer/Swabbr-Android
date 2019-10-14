package com.laixer.sample.presentation.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.laixer.navigation.features.SampleNavigation
import com.laixer.sample.R
import com.laixer.sample.injectFeature
import com.laixer.sample.presentation.loadAvatar
import com.laixer.sample.presentation.model.ProfileItem
import com.laixer.sample.presentation.model.VlogItem
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_vlog_details.*
import kotlinx.android.synthetic.main.include_user_info.*
import org.koin.androidx.viewmodel.ext.viewModel

class ProfileActivity : AppCompatActivity() {

    private val vm: ProfileViewModel by viewModel()
    private val userId by lazy { intent.getStringExtra(SampleNavigation.USER_ID_KEY) }
    private val snackBar by lazy {
        Snackbar.make(container, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) { vm.get(userId) }
    }
    //private val adapter = ProfileAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        injectFeature()

        if (savedInstanceState == null) {
            vm.get(userId)
        }

        //profilevlogsRecyclerView.isNestedScrollingEnabled = false
        //profilevlogsRecyclerView.adapter = adapter

        vm.profile.observe(this, Observer { updateProfile(it) })
        //vm.profile.observe(this, Observer { updateProfileVlogs(it.vlogs) })
    }

    private fun updateProfile(profileItem: ProfileItem?) {
        profileItem?.let {
            userAvatar.loadAvatar(it.user.id)
            userUsername.text = "@${it.user.nickname}"
            userName.text = "${it.user.firstName} ${it.user.lastName}"
        }
    }

    private fun updateProfileVlogs(resource: List<VlogItem>?) {
        resource?.let {
            //adapter.submitList(it)
            snackBar.show()
        }
    }
}
