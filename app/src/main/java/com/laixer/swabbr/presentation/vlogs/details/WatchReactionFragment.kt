package com.laixer.swabbr.presentation.vlogs.details

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.data.datasource.model.WatchReactionResponse
import com.laixer.swabbr.presentation.reaction.ReactionViewModel
import kotlinx.android.synthetic.main.item_vlog.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class WatchReactionFragment(id: String? = null) : VideoFragment() {
    private val args by navArgs<WatchReactionFragmentArgs>()

    private val reactionVm: ReactionViewModel by viewModel()
    private val reactionId by lazy { UUID.fromString(id ?: args.reactionId) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        reactionVm.watchReactionResponse.observe(viewLifecycleOwner, Observer { start(it) })
        return layoutInflater.inflate(R.layout.item_vlog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reactionVm.watch(reactionId)
    }

    private fun start(res: Resource<WatchReactionResponse>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {
                content_loading_progressbar.visibility = View.VISIBLE
            }
            ResourceState.SUCCESS -> {
                data?.let {
                    stream(it.endpointUrl, it.token)

                }
            }
            ResourceState.ERROR -> {
            }
        }
    }

    companion object {
        private const val TAG = "VlogFragment"

        fun create(reactionId: String): WatchReactionFragment {
            return WatchReactionFragment(reactionId)
        }
    }
}
