package com.laixer.sample.presentation.vlogdetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.laixer.navigation.features.SampleNavigation
import com.laixer.sample.R
import com.laixer.sample.injectFeature
import com.laixer.sample.presentation.loadAvatar
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.sample.presentation.model.ReactionItem
import com.laixer.sample.presentation.model.VlogItem
import kotlinx.android.synthetic.main.activity_vlog_details.*
import kotlinx.android.synthetic.main.include_user_info.*
import kotlinx.android.synthetic.main.item_list_vlog.*
import org.koin.androidx.viewmodel.ext.viewModel

class VlogDetailsActivity : AppCompatActivity() {

    private val vm: VlogDetailsViewModel by viewModel()
    private val adapter = ReactionsAdapter()
    private val userId by lazy { intent.getStringExtra(SampleNavigation.USER_ID_KEY) }
    private val vlogId by lazy { intent.getStringExtra(SampleNavigation.VLOG_ID_KEY) }
    private val snackBar by lazy {
        Snackbar.make(container, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) { vm.getReactions(vlogId, refresh = true) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vlog_details)

        injectFeature()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        reactionsRecyclerView.isNestedScrollingEnabled = false
        reactionsRecyclerView.adapter = adapter

        if (savedInstanceState == null) {
            vm.getVlogs(UserIdVlogId(userId, vlogId))
            vm.getReactions(vlogId, refresh = false)
        }

        vm.vlogs.observe(this, Observer { updateVlogs(it) })
        vm.reactions.observe(this, Observer { updateReactions(it) })
    }

    private fun updateVlogs(vlogItem: VlogItem?) {
        vlogItem?.let {
            userAvatar.loadAvatar(it.userId)
            userUsername.text = "@${it.nickname}"
            userName.text = "${it.firstName} ${it.lastName}"
            vlogPostDate.text = it.startDate
            vlogDuration.text = it.duration
        }
    }

    private fun updateReactions(resource: Resource<List<ReactionItem>>?) {
        resource?.let {
            when (it.state) {
                ResourceState.LOADING -> progressBar.visible()
                ResourceState.SUCCESS -> progressBar.gone()
                ResourceState.ERROR -> progressBar.gone()
            }
            it.data?.let { adapter.submitList(it) }
            it.message?.let { snackBar.show() }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
