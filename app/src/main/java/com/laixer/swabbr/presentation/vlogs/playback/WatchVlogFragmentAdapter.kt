package com.laixer.swabbr.presentation.vlogs.playback

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.video.WatchVideoFragmentAdapter

// TODO Dependent on mutable live data, is this desired? Maybe too coupled...
/**
 *  Adapter that creates a [WatchVlogFragment] for each vlog
 *  so the vlog can be played back using this fragment.
 */
internal class WatchVlogFragmentAdapter(
    fragment: Fragment,
    /** The resource that will be observed. */
    private val vlogListResource: MutableLiveData<Resource<List<VlogWrapperItem>>>
) : WatchVideoFragmentAdapter(fragment) {
    /**
     *  Creates a new [WatchVlogFragment] for vlog playback.
     */
    override fun createFragment(position: Int): Fragment =
        vlogListResource.value!!.data!![position].vlog.id.let {
            WatchVlogFragment.create(vlogId = it.toString())
        }

    /**
     *  Gets the total count in the [vlogListResource] or 0 if
     *  no vlogs are present.
     */
    override fun getItemCount(): Int = vlogListResource.value?.data?.size ?: 0
}
