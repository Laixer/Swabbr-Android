package com.laixer.swabbr.presentation.vlogs.details

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.data.datasource.model.WatchLivestreamResponse
import com.laixer.swabbr.presentation.streaming.StreamViewModel
import kotlinx.android.synthetic.main.item_vlog.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class WatchStreamFragment(streamId: String? = null) : InteractiveVideoFragment() {

    private val args by navArgs<WatchStreamFragmentArgs>()

    private val liveVm: StreamViewModel by viewModel()
    private val livestreamId: String by lazy { streamId ?: args.livestreamId }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        liveVm.watchResponse.observe(viewLifecycleOwner, Observer { start(it) })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        liveVm.watch(livestreamId)
    }

    private fun start(res: Resource<WatchLivestreamResponse>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {
                content_loading_progressbar.visibility = View.VISIBLE
            }
            ResourceState.SUCCESS -> {
                data?.let {
                    vlogVm.getVlog(UUID.fromString(it.liveVlogId))
                    stream(it.endpointUrl, it.token)
                }
            }
            ResourceState.ERROR -> {
            }
        }
    }

    companion object {
        private const val TAG = "WatchStreamFragment"

        fun create(livestreamId: String): WatchStreamFragment {
            return WatchStreamFragment(livestreamId)
        }
    }
}
