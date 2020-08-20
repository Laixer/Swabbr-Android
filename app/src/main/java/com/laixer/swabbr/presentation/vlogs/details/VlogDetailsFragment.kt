package com.laixer.swabbr.presentation.vlogs.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.Utils.enterFullscreen
import com.laixer.swabbr.presentation.Utils.exitFullscreen
import com.laixer.swabbr.presentation.model.UserVlogItem
import kotlinx.android.synthetic.main.fragment_vlog_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

class VlogDetailsFragment : AuthFragment() {

    private val vm: VlogDetailsViewModel by viewModel()
    private val args by navArgs<VlogDetailsFragmentArgs>()
    private val vlogId by lazy { args.vlogId }
    private val userId by lazy { args.userId }
    private val livestreamId by lazy { args.livestreamId }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_vlog_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectFeature()

        // Update vlogs and reactions when the dataset changes
        vm.vlogs.observe(viewLifecycleOwner, Observer { updateVlogs(it) })

        vlog_viewpager.run {
            // Add a fragment adapter to the ViewPager to manage fragments
            adapter = object : FragmentStateAdapter(this@VlogDetailsFragment) {
                override fun createFragment(position: Int): Fragment =
                    VlogFragment.create(vm.vlogs.value!!.data!![position], livestreamId)

                override fun getItemCount(): Int = vm.vlogs.value?.data?.size ?: 0
            }
        }

        if (userId != null) {
            vm.getVlogs(UUID.fromString(userId), refresh = true)
        } else {
            vm.getVlog(UUID.fromString(vlogId), refresh = true)
        }
    }

    private fun updateVlogs(resource: Resource<List<UserVlogItem>?>) = with(resource) {
        when (state) {
            ResourceState.LOADING -> {
            }
            ResourceState.SUCCESS -> {
                data?.let {
                    val index =
                        vm.vlogs.value?.data?.indexOf(vm.vlogs.value?.data?.first { item -> item.vlogId.toString() == vlogId })
                            ?: 0

                    vlog_viewpager?.currentItem = index
                    vlog_viewpager?.adapter?.notifyDataSetChanged()
                }
            }
            ResourceState.ERROR -> {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enterFullscreen(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        exitFullscreen(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exitFullscreen(requireActivity())
    }

    companion object {
        private const val TAG = "VlogDetailsFragment"
    }
}


