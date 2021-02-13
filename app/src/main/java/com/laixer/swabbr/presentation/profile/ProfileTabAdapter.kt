package com.laixer.swabbr.presentation.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

/**
 *  Adapter for the tabs of a profile.
 *
 *  @param fragment The fragment in which this will exist.
 *  @param isSelf True if we are in the authenticated users profile.
 */
internal class ProfileTabAdapter(
    fragment: Fragment,
    private val userId: UUID,
    private val isSelf: Boolean
) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        if (isSelf) {
            return when (position) {
                0 -> ProfileVlogsFragment(userId)
                1 -> ProfileDetailsFragment(userId)
                2 -> ProfileFollowingFragment(userId)
                3 -> ProfileFollowersFragment(userId)
                else -> ProfileDetailsFragment(userId)
            }
        } else {
            return when (position) {
                0 -> ProfileVlogsFragment(userId)
                1 -> ProfileFollowingFragment(userId)
                2 -> ProfileFollowersFragment(userId)
                else -> ProfileDetailsFragment(userId)
            }
        }
    }

    override fun getItemCount(): Int = 4
}
