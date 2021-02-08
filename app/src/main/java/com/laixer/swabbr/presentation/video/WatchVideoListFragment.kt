package com.laixer.swabbr.presentation.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.AuthFragment
import kotlinx.android.synthetic.main.fragment_video_view_pager.*

// TODO What to display if we have no content?
// TODO Swipe refresh layout?
// TODO Loading icon?
// TODO Fix position storing, not sure this works correctly.
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
     *  Sets the [watchVideoFragmentAdapter] as the adapter for the
     *  [video_viewpager]. Call this, then retrieve any resources
     *  using an override of this method.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        video_viewpager.apply {
            adapter = getWatchVideoFragmentAdapter()
        }
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

    /**
     *  Called when we re-enter this fragment. This is used to
     *  store the vlog which we were watching before.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        with(savedInstanceState?.getInt(CURRENT_ITEM_INDEX) ?: 0) {
            video_viewpager.currentItem = this
        }

        super.onViewStateRestored(savedInstanceState)
    }

    /**
     *  Called when we exit this fragment. This stores the
     *  vlog we are currently watching.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        // TODO This used to crash saying vlog_viewpager was
        //  null. This call was added to save this, some bugs
        //  might originate here though.
        if (video_viewpager == null) {
            return
        }

        outState.putInt(CURRENT_ITEM_INDEX, video_viewpager.currentItem)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        video_viewpager.adapter = null
    }

    companion object {
        private const val CURRENT_ITEM_INDEX = "CURRENTITEMINDEX"
        private const val TAG = "WatchUserVlogsFragment"
    }
}


