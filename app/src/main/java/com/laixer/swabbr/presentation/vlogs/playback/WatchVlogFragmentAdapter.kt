package com.laixer.swabbr.presentation.vlogs.playback

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.laixer.swabbr.presentation.vlogs.list.VlogListViewModel

// TODO Bad design, dependency on the vm with this positioning.
/**
 *  Adapter that creates a [WatchVlogFragment] for each vlog
 *  so the vlog can be played back using this fragment.
 */
internal class WatchVlogFragmentAdapter(
    fragment: Fragment,
    private val vlogListVm: VlogListViewModel
) : FragmentStateAdapter(fragment) {
    /**
     *  Creates a new [WatchVlogFragment] for a vlog to watch.
     */
    override fun createFragment(position: Int): Fragment =
        vlogListVm.vlogs.value!!.data!![position].vlog.id.let {
            WatchVlogFragment.create(vlogId = it.toString())
        }

    override fun getItemCount(): Int = vlogListVm.vlogs.value?.data?.size ?: 0
}
