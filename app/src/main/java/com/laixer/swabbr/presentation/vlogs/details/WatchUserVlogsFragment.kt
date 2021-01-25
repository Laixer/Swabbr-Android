package com.laixer.swabbr.presentation.vlogs.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import kotlinx.android.synthetic.main.fragment_vlogs_pager.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class WatchUserVlogsFragment : AuthFragment() {

    private val vm: VlogDetailsViewModel by viewModel()
    private val args by navArgs<WatchUserVlogsFragmentArgs>()
    private val userId by lazy { args.userId }
    private val initialVlogId by lazy { args.initialVlogId }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vm.vlogs.observe(viewLifecycleOwner, Observer { updateVlogs(it) })
        return inflater.inflate(R.layout.fragment_vlogs_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vlog_viewpager.apply {
            adapter = FragmentAdapter()
//            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                override fun onPageSelected(position: Int) {
//                    currentItem = position
//                    super.onPageSelected(position)
//                }
//            })
        }

        vm.getVlogsForUser(UUID.fromString(userId), refresh = true)
    }

    private fun updateVlogs(resource: Resource<List<VlogWrapperItem>>) = with(resource) {
        when (state) {
            ResourceState.LOADING -> {
            }
            ResourceState.SUCCESS -> {
                data?.let {
                    initialVlogId?.let {
                        vlog_viewpager.currentItem = vm.vlogs.value?.data?.indexOf(vm.vlogs.value?.data?.first { item ->
                            item.vlog.id.toString() == it
                        }) ?: 0
                    }

                    vlog_viewpager.adapter?.notifyDataSetChanged()
                }
            }
            ResourceState.ERROR -> {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        with(savedInstanceState?.getInt(CURRENT_ITEM_INDEX) ?: 0) {
            vlog_viewpager.currentItem = this
        }

        super.onViewStateRestored(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CURRENT_ITEM_INDEX, vlog_viewpager.currentItem)
        super.onSaveInstanceState(outState)
    }

    internal inner class FragmentAdapter : FragmentStateAdapter(this@WatchUserVlogsFragment) {
        override fun createFragment(position: Int): Fragment =
            getVlogAtIndex(position).vlog.id.let { WatchVlogFragment.create(it.toString()) }

        override fun getItemCount(): Int = vm.vlogs.value?.data?.size ?: 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vlog_viewpager.adapter = null
    }

    private fun getVlogAtIndex(index: Int): VlogWrapperItem = vm.vlogs.value!!.data!![index]

    companion object {
        private const val CURRENT_ITEM_INDEX = "CURRENTITEMINDEX"
        private const val TAG = "WatchUserVlogsFragment"
    }
}


