package com.laixer.swabbr.presentation.vlogs.details

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.presentation.model.VlogItem
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import kotlinx.android.synthetic.main.item_vlog.*
import java.util.*

/**
 *  Fragment for watching a vlog.
 */
class WatchVlogFragment(id: String? = null) : InteractiveVideoFragment() {
    private val args by navArgs<WatchVlogFragmentArgs>()
    private val vlogId: UUID by lazy { UUID.fromString(id ?: args.vlogId) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vlogVm.watchVlogResponse.observe(viewLifecycleOwner, Observer { start(it) })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vlogVm.watch(vlogId)
        vlogVm.getVlog(vlogId)
    }

    /**
     *  This will start the vlog playback.
     *
     *  @param res Vlog wrapper containing [VlogItem.videoUri].
     */
    private fun start(res: Resource<VlogWrapperItem>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {
                content_loading_progressbar.visibility = View.VISIBLE
            }
            ResourceState.SUCCESS -> {
                data?.let {
                    stream(it.vlog.videoUri.toString())
                }
            }
            ResourceState.ERROR -> {
            }
        }
    }

    companion object {
        private const val VLOG_ID_KEY = "VLOGIDKEY"

        fun create(vlogId: String): WatchVlogFragment {
            return WatchVlogFragment(vlogId)
        }
    }
}
