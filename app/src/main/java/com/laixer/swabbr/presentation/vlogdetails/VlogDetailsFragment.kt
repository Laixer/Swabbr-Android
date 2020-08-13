package com.laixer.swabbr.presentation.vlogdetails

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
import com.laixer.swabbr.presentation.model.UserVlogItem
import kotlinx.android.synthetic.main.activity_app.*
import kotlinx.android.synthetic.main.fragment_vlog_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

class VlogDetailsFragment : AuthFragment() {

    private val vm: VlogDetailsViewModel by viewModel()
    private val args by navArgs<VlogDetailsFragmentArgs>()
    private val vlogId by lazy { args.vlogId }
    private val userId by lazy { args.userId }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_vlog_details, container, false).apply {
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        injectFeature()
        vm.run {
            // Update vlogs and reactions when the dataset changes
            vlogs.observe(viewLifecycleOwner, Observer { updateVlogs(it) })
        }

        vlog_viewpager.run {
            // Add a fragment adapter to the ViewPager to manage fragments
            adapter = object : FragmentStateAdapter(this@VlogDetailsFragment) {
                override fun createFragment(position: Int): Fragment =
                    VlogFragment.create(vm.vlogs.value!!.data!![position])

                override fun getItemCount(): Int = vm.vlogs.value?.data?.size ?: 0
            }
        }

        vm.getVlogs(UUID.fromString(userId), refresh = true)
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
        hideUI()
    }

    override fun onPause() {
        super.onPause()
        resetUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resetUI()
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

    private fun resetUI() {
        requireActivity().apply {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_FULLSCREEN)
            toolbar.visibility = View.VISIBLE
            bottom_nav.visibility = View.VISIBLE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    companion object {
        private const val TAG = "VlogDetailsFragment"
    }
}


