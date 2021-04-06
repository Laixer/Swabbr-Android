package com.laixer.swabbr.presentation.vlogs.playback

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.types.VideoPlaybackState
import com.laixer.swabbr.presentation.video.WatchVideoFragmentAdapter
import java.util.*

// TODO Duplicate functionality with WatchReactionFragmentAdapter - move up.
/**
 *  Adapter that creates a [WatchVlogFragment] for each vlog
 *  so the vlog can be played back using this fragment.
 *
 *  @param fragment The fragment from which this is created.
 *  @param vlogListResource The resource that will be observed.
 *  @param onVideoCompletedCallback Optional callback when the playback state changes.
 */
internal class WatchVlogFragmentAdapter(
    private val fragment: Fragment,
    /** The resource that will be observed. */
    private val vlogListResource: MutableLiveData<Resource<List<VlogWrapperItem>>>,
    private val onVideoCompletedCallback: ((vlogId: UUID, position: Int, state: VideoPlaybackState) -> Unit)? = null
) : WatchVideoFragmentAdapter(fragment) {
    /**
     *  Creates a new [WatchVlogFragment] for vlog playback.
     */
    override fun createFragment(position: Int): Fragment =
        vlogListResource.value!!.data!![position].vlog.id.let { vlogId ->
            val createdFragment = WatchVlogFragment.newInstance(vlogId)

            // Attach the callback method if we have one.
            onVideoCompletedCallback?.let { callback ->
                /**
                 *  Note that we use [fragment] to get the view lifecycle owner,
                 *  not the created fragment since that object may not be fully
                 *  inflated when calling this.
                 */
                if (fragment.view != null) {
                    createdFragment.videoStateLiveData.observe(fragment.viewLifecycleOwner, Observer { state ->
                        callback(vlogId, position, state)
                    })
                }
            }

            return createdFragment
        }

    /**
     *  Gets the total count in the [vlogListResource] or 0 if
     *  no vlogs are present.
     */
    override fun getItemCount(): Int = vlogListResource.value?.data?.size ?: 0
}
