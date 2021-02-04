package com.laixer.swabbr.presentation.reaction

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.laixer.swabbr.presentation.vlogs.playback.VlogViewModel
import com.laixer.swabbr.presentation.vlogs.playback.WatchVlogFragment

// TODO Use
// TODO Bad design, dependency on the vm with this positioning.
/**
 *  Adapter that creates a [WatchReactionFragment] for each reaction
 *  so the reaction can be played back using this fragment.
 */
internal class WatchReactionFragmentAdapter(
    fragment: Fragment,
    // TODO Maybe separate VM?
    private val vlogVm: VlogViewModel
) : FragmentStateAdapter(fragment) {
    /**
     *  Creates a new [WatchVlogFragment] for a vlog to watch.
     */
    override fun createFragment(position: Int): Fragment =
        vlogVm.reactions.value!!.data!![position].reaction.id.let {
            WatchReactionFragment.create(reactionId = it.toString())
        }

    override fun getItemCount(): Int = vlogVm.reactions.value?.data?.size ?: 0
}
