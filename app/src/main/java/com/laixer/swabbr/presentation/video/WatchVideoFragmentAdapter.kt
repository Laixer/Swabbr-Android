package com.laixer.swabbr.presentation.video

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

// TODO Do we really need this abstraction?
/**
 *  Abstract adapter to display a video on full screen.
 *  Implement this to use with [WatchVideoListFragment],
 *  so each video will be inflated using this adapter.
 */
abstract class WatchVideoFragmentAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment)
