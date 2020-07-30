package com.laixer.swabbr.presentation.vlogdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.gone
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.UserVlogItem
import kotlinx.android.synthetic.main.activity_app.*
import kotlinx.android.synthetic.main.fragment_vlog_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

class VlogDetailsFragment : AuthFragment() {

    private val vm: VlogDetailsViewModel by viewModel()
    private var reactionsAdapter: ReactionsAdapter? = null
    private val args by navArgs<VlogDetailsFragmentArgs>()
    private val vlogId by lazy { args.vlogId }
    private val userId by lazy { args.userId }
    private lateinit var currentVlog: UserVlogItem
    private val snackBar by lazy {
        Snackbar.make(container, getString(R.string.error), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.retry)) {
                vm.getReactions(
                    currentVlog.vlogId, refresh = true
                )
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_vlog_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideUI()

        reactionsAdapter = ReactionsAdapter(requireContext())

        injectFeature()

        reactionsRecyclerView.run {
            isNestedScrollingEnabled = false
            adapter = reactionsAdapter
        }

        vm.run {
            // Update vlogs and reactions when the dataset changes
            vlogs.observe(viewLifecycleOwner, Observer { updateVlogs(it) })
            reactions.observe(viewLifecycleOwner, Observer { updateReactions(it) })
        }

        if (savedInstanceState == null) {
            vm.getVlogs(UUID.fromString(userId), refresh = true)
            vm.getReactions(UUID.fromString(vlogId), refresh = true)
        }

        vlog_viewpager.run {
            // Add a fragment adapter to the ViewPager to manage fragments
            adapter = object : FragmentStateAdapter(this@VlogDetailsFragment) {
                override fun createFragment(position: Int): Fragment =
                    VlogFragment.create(vm.vlogs.value!!.data!![position])

                override fun getItemCount(): Int = vm.vlogs.value?.data?.size ?: 0
            }
            // Add a listener to the ViewPager for when a new page (vlog) is selected through a swipe action
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentVlog = vm.vlogs.value!!.data!![position]
                    vm.getReactions(currentVlog.vlogId)
                }
            })
        }

        vm.getVlogs(UUID.fromString(userId), refresh = false)
        vm.getReactions(UUID.fromString(vlogId), refresh = false)
    }

    private fun hideUI() {
        requireActivity().window.run {
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        requireActivity().toolbar.visibility = View.GONE
        requireActivity().bottom_nav.visibility = View.GONE
        activity?.window?.decorView?.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun updateVlogs(resource: Resource<List<UserVlogItem>?>) = with(resource) {
        when (state) {
            ResourceState.LOADING -> {
            }
            ResourceState.SUCCESS -> {
                data?.let {
                    vlog_viewpager?.currentItem =
                        vm.vlogs.value?.data?.indexOf(vm.vlogs.value?.data?.first { item -> item.vlogId.toString() == vlogId })
                            ?: 0
                    vlog_viewpager?.adapter?.notifyDataSetChanged()
                }
            }
            ResourceState.ERROR -> {
                message?.let { snackBar.setText(it).show() }
            }
        }
    }

    private fun updateReactions(resource: Resource<List<ReactionItem>?>) {
        resource.run {
            progressBar.run {
                when (state) {
                    ResourceState.LOADING -> visible()
                    ResourceState.SUCCESS -> {
                        data?.let { reactionsAdapter?.submitList(it) }
                        gone()
                    }
                    ResourceState.ERROR -> {
                        message?.let { snackBar.show() }
                        gone()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        reactionsAdapter = null

        requireActivity().apply {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            toolbar.visibility = View.VISIBLE
            bottom_nav.visibility = View.VISIBLE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }
}


