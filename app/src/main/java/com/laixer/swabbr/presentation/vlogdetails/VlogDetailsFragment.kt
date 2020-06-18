package com.laixer.swabbr.presentation.vlogdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
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
import kotlinx.android.synthetic.main.fragment_vlog_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

class VlogDetailsFragment : AuthFragment() {

    private val vm: VlogDetailsViewModel by viewModel()
    private var reactionsAdapter: ReactionsAdapter? = null
    private val args by navArgs<VlogDetailsFragmentArgs>()
    private val vlogIds by lazy { args.vlogIds }
    private val selectedId by lazy { args.selectedId }
    private var vlogs = listOf<UserVlogItem>()
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
            vm.getVlogs(vlogIds.map { UUID.fromString(it) }, refresh = true)
            vm.getReactions(UUID.fromString(selectedId), refresh = true)
        }

        vlog_viewpager.run {
            // Add a fragment adapter to the ViewPager to manage fragments
            adapter = object : FragmentStateAdapter(this@VlogDetailsFragment) {
                override fun createFragment(position: Int): Fragment = VlogFragment.create(vlogs[position])
                override fun getItemCount(): Int = vlogs.size
            }
            // Add a listener to the ViewPager for when a new page (vlog) is selected through a swipe action
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentVlog = vlogs[position]
                    vm.getReactions(currentVlog.vlogId)
                }
            })
        }

        vm.getVlogs(vlogIds.map { UUID.fromString(it) }, refresh = false)
        vm.getReactions(UUID.fromString(selectedId), refresh = false)
    }

    private fun updateVlogs(resource: Resource<List<UserVlogItem>?>) = with(resource) {
        when (state) {
            ResourceState.LOADING -> {
            }
            ResourceState.SUCCESS -> {
                data?.let {
                    vlogs = it
                    vlog_viewpager?.currentItem =
                        vlogs.indexOf(vlogs.first { item -> item.vlogId.toString() == selectedId })
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
    }
}

