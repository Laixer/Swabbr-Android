package com.laixer.swabbr.presentation.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

/**
 *  Adapter for the tabs of a profile for a generic user.
 *  When displaying self, use [ProfileTabSelfAdapter].
 *
 *  @param fragment The fragment in which this will exist.
 */
internal class ProfileTabAdapter(
    fragment: Fragment,
    private val userId: UUID
) : FragmentStateAdapter(fragment) {
    /**
     *  Selects which tab belongs to which fragment.
     */
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileVlogsFragment(userId)
            1 -> ProfileFollowingFragment(userId)
            2 -> ProfileFollowersFragment(userId)
            else -> ProfileVlogsFragment(userId)
        }
    }

    /**
     *  We always have 3 tabs.
     */
    override fun getItemCount(): Int = ITEM_COUNT

    internal companion object {
        const val ITEM_COUNT = 3
    }
}
