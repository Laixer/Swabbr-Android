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
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.UserVlogItem
import kotlinx.android.synthetic.main.fragment_vlog_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

class VlogDetailsFragment : Fragment() {

    private val vm: VlogDetailsViewModel by viewModel()
    private var reactionsAdapter: ReactionsAdapter? = null
    private val args by navArgs<VlogDetailsFragmentArgs>()
    private val vlogIds by lazy { args.vlogIds }
    private var vlogs = listOf<UserVlogItem>()
    private lateinit var currentVlog: UserVlogItem
    private val snackBar by lazy {
        Snackbar.make(container, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
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

        if (savedInstanceState == null) {
            vm.getVlogs(vlogIds.map { UUID.fromString(it) })
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

        vm.run {
            // Update vlogs and reactions when the dataset changes
            vlogs.observe(viewLifecycleOwner, Observer { updateVlogs(it) })
            reactions.observe(viewLifecycleOwner, Observer { updateReactions(it) })
        }
    }

    private fun updateVlogs(resource: Resource<List<UserVlogItem>?>) = resource.data?.let {
        vlogs = it
        vlog_viewpager?.adapter?.notifyDataSetChanged()
    }

    private fun updateReactions(resource: Resource<List<ReactionItem>?>) {
        resource.run {
            progressBar.run {
                when (state) {
                    ResourceState.LOADING -> visible()
                    ResourceState.SUCCESS -> gone()
                    ResourceState.ERROR -> gone()
                }
            }
            data?.let { reactionsAdapter?.submitList(it) }
//            message?.let { snackBar.show() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        reactionsAdapter = null
    }
}
