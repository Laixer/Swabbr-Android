package com.laixer.swabbr.presentation.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.laixer.presentation.gone
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.auth.AuthFragment
import kotlinx.android.synthetic.main.fragment_video_view_pager.*

// TODO Swipe refresh layout?
// TODO Loading icon?
/**
 *  This fragment is used to watch a video and to be able to swipe left
 *  and right to go to other videos. When entering this fragment, the
 *  entire fragment is filled with the [video_viewpager].
 *
 *  Note that each video in the [video_viewpager] is inflated using a
 *  [WatchVideoFragmentAdapter] implementation.
 */
abstract class WatchVideoListFragment : AuthFragment() {
    /**
     *  Inflates the view pager.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video_view_pager, container, false)
    }

    /**
     *  Sets the [WatchVideoFragmentAdapter] as the adapter for the
     *  [video_viewpager]. Call this, then retrieve any resources
     *  using an override of this method.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the empty collection message
        text_display_empty_video_collection.gone()

        /**
         *  Keep one item at either side of each item, no more. This prevents
         *  us from creating too many [WatchVideoFragment] instances which each
         *  have their own ExoPlayer instance. Too many player instances will
         *  make the UI stop behaving properly due to limited encoder resources.
         */
        video_viewpager.offscreenPageLimit = 1
        video_viewpager.adapter = getWatchVideoFragmentAdapter()
    }

    // TODO Can we do this more elegantly?
    /**
     *  Override this function to clarify which implementation
     *  of [WatchVideoFragmentAdapter] should be used an how
     *  it should be instantiated. This will be called in the
     *  [onViewCreated] function, you do not need to assign it
     *  as the adapter for [video_viewpager] yourself.
     */
    protected abstract fun getWatchVideoFragmentAdapter(): WatchVideoFragmentAdapter
}


