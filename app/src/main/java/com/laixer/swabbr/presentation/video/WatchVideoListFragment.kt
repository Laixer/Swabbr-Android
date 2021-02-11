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
     *  TODO This is a workaround because the fragment lifecycle for Android is idiotic.
     *  Stores the currently watched item in this viewpager.
     */
    //private var currentIndex: Int? = null

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

    /**
     *  Called when we re-enter this fragment. This is used to
     *  store the vlog which we were watching before.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        // TODO Beunfix
//        if (currentIndex != null) {
//            video_viewpager.currentItem = currentIndex!!
//        } else {
//            val i = 0
//        }

        super.onViewStateRestored(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        video_viewpager.adapter = null
    }

    override fun onStop() {
        // TODO Beunfix
        //currentIndex = video_viewpager.currentItem

        super.onStop()
    }

    companion object {
        //protected const val CURRENT_ITEM_INDEX = "CURRENT_ITEM_INDEX"
    }
}


