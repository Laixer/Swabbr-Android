package com.laixer.swabbr.presentation.reaction.playback

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.types.VideoPlaybackState
import com.laixer.swabbr.presentation.video.WatchVideoFragmentAdapter
import java.util.*

/**
 *  Adapter that creates a [WatchReactionFragment] for each reaction
 *  so the reaction can be played back using this fragment.
 *
 *  TODO Move observe functionality to the [WatchVideoFragmentAdapter] when we
 *       need it. A design challenge will be how to handle id's. Do we even
 *       need these in the first place? ...
 *  This adapter is responsible for ensuring that [onVideoCompletedCallback]
 *  will observe all [WatchReactionFragment.videoStateLiveData] resources.
 *  Call
 *
 *  @param fragment The fragment from which this is created.
 *  @param reactionListResource The resource that will be observed.
 *  @param onVideoCompletedCallback Optional callback when the playback state changes.
 */
internal class WatchReactionFragmentAdapter(
    private val fragment: Fragment,
    private val reactionListResource: MutableLiveData<Resource<List<ReactionWrapperItem>>>,
    private val onVideoCompletedCallback: ((reactionId: UUID, position: Int, state: VideoPlaybackState) -> Unit)? = null
) : WatchVideoFragmentAdapter(fragment) {
    /**
     *  Creates a new [WatchReactionFragment] for reaction playback.
     */
    override fun createFragment(position: Int): Fragment =
        reactionListResource.value!!.data!![position].reaction.id.let { reactionId ->
            val createdFragment = WatchReactionFragment.newInstance(reactionId)

            // Attach the callback method if we have one.
            onVideoCompletedCallback?.let { callback ->
                /**
                 *  Note that we use [fragment] to get the view lifecycle owner,
                 *  not the created fragment since that object may not be fully
                 *  inflated when calling this.
                 */
                if (fragment.view != null) {
                    createdFragment.videoStateLiveData.observe(fragment.viewLifecycleOwner, Observer { state ->
                        callback(reactionId, position, state)
                    })
                }
            }

            return createdFragment
        }

    /**
     *  Gets the total count in the [reactionListResource] or 0 if
     *  no reactions are present.
     */
    override fun getItemCount(): Int = reactionListResource.value?.data?.size ?: 0
}
