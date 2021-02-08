package com.laixer.swabbr.presentation.reaction

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.laixer.presentation.Resource
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.video.WatchVideoFragmentAdapter

// TODO Use
// TODO Dependent on mutable live data, is this desired? Maybe too coupled...
/**
 *  Adapter that creates a [WatchReactionFragment] for each reaction
 *  so the reaction can be played back using this fragment.
 */
internal class WatchReactionFragmentAdapter(
    fragment: Fragment,
    /** The resource that will be observed. */
    private val reactionListResource: MutableLiveData<Resource<List<ReactionWrapperItem>>>
) : WatchVideoFragmentAdapter(fragment) {
    /**
     *  Creates a new [WatchReactionFragment] for reaction playback.
     */
    override fun createFragment(position: Int): Fragment =
        reactionListResource.value!!.data!![position].reaction.id.let {
            WatchReactionFragment.create(reactionId = it.toString())
        }

    /**
     *  Gets the total count in the [reactionListResource] or 0 if
     *  no reactions are present.
     */
    override fun getItemCount(): Int = reactionListResource.value?.data?.size ?: 0
}
