package com.laixer.swabbr.presentation.reaction

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.vlogs.details.VideoFragment
import com.laixer.swabbr.presentation.reaction.WatchReactionFragmentArgs
import kotlinx.android.synthetic.main.item_vlog.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

/**
 *  Wrapper around [VideoFragment] used for [ReactionItem] playback.
 */
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

    /**
     *  Attempts to start video playback.
     *
     *  @param res Reaction resource item containing [ReactionItem.videoUri].
     */
    private fun start(res: Resource<ReactionWrapperItem>) = with(res) {
        when (state) {
            ResourceState.LOADING -> {
                content_loading_progressbar.visibility = View.VISIBLE
            }
            ResourceState.SUCCESS -> {
                data?.let {
                    stream(it.reaction.videoUri!!)
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
