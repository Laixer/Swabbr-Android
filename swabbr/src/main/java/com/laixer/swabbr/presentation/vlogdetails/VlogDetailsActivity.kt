package com.laixer.swabbr.presentation.vlogdetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.laixer.navigation.features.SwabbrNavigation
import com.laixer.swabbr.injectFeature
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.presentation.model.ProfileVlogItem
import com.laixer.swabbr.presentation.model.ReactionItem
import kotlinx.android.synthetic.main.activity_vlog_details.*
import org.koin.androidx.viewmodel.ext.viewModel
import com.laixer.swabbr.R

class VlogDetailsActivity : AppCompatActivity() {

    private val vm: VlogDetailsViewModel by viewModel()
    private val adapter = ReactionsAdapter()

    private val vlogIds by lazy { intent.getStringArrayListExtra(SwabbrNavigation.VLOG_ID_KEYS) }
    private var vlogs = listOf<ProfileVlogItem>()

    private lateinit var currentVlog: ProfileVlogItem

    private val snackBar by lazy {
        Snackbar.make(container, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) {
                vm.getReactions(
                    currentVlog.vlogId,
                    refresh = true
                )
            }
    }

    private lateinit var vp: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vlog_details)

        injectFeature()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        reactionsRecyclerView.isNestedScrollingEnabled = false
        reactionsRecyclerView.adapter = adapter

        if (savedInstanceState == null) {
            vm.getVlogs(vlogIds)
        }

        // Register ViewPager
        vp = vlog_viewpager

        // Add a fragment adapter to the ViewPager to manage fragments
        vp.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment =
                VlogFragment.create(vlogs[position])

            override fun getItemCount(): Int = vlogs.size
        }

        // Add a listener to the ViewPager for when a new page (vlog) is selected through a swipe action
        vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentVlog = vlogs[position]
                vm.getReactions(currentVlog.vlogId)
            }
        })

//        resizeScreen(resources.configuration)

        // Update vlogs and reactions when the dataset changes
        vm.vlogs.observe(this, Observer { updateVlogs(it) })
        vm.reactions.observe(this, Observer { updateReactions(it) })
    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        resizeScreen(newConfig)
//        super.onConfigurationChanged(newConfig)
//    }

//    fun resizeScreen(config: Configuration) {
//        if (::vp.isInitialized) {
//            val screenSize = Point()
//            windowManager.defaultDisplay.getSize(screenSize)
//            when (config.orientation) {
//                Configuration.ORIENTATION_LANDSCAPE ->
//                vp.layoutParams = LinearLayout.LayoutParams(screenSize.x, screenSize.y)
//                Configuration.ORIENTATION_PORTRAIT ->
//                vp.layoutParams = LinearLayout.LayoutParams(screenSize.x, screenSize.y)
//            }
//        }
//    }

    private fun updateVlogs(resource: Resource<List<ProfileVlogItem>?>) = resource.data?.let {
        vlogs = it
        vp.adapter?.notifyDataSetChanged()
    }

    private fun updateReactions(resource: Resource<List<ReactionItem>?>) {
        when (resource.state) {
            ResourceState.LOADING -> progressBar.visible()
            ResourceState.SUCCESS -> progressBar.gone()
            ResourceState.ERROR -> progressBar.gone()
        }
        resource.data?.let { adapter.submitList(it) }
        resource.message?.let { snackBar.show() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
