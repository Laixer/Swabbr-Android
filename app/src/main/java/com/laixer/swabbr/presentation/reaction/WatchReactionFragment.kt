package com.laixer.swabbr.presentation.reaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.video.VideoFragment
import com.laixer.swabbr.presentation.vlogs.playback.VlogViewModel
import kotlinx.android.synthetic.main.fragment_video.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

// TODO FIX
/**
 *  Wrapper around a single [VideoFragment] used for [ReactionItem] playback.
 */
class WatchReactionFragment(id: String? = null) : VideoFragment() {
    private val args by navArgs<WatchReactionFragmentArgs>()

    // TODO Correct vm?
    private val reactionVm: ReactionViewModel by viewModel()
    private val reactionId by lazy { UUID.fromString(id ?: args.reactionId) }

    /**
     *  Assign observers to [reactionVm].
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //reactionVm.reactions.observe(viewLifecycleOwner, Observer { onReactionLoaded(it) })
        return layoutInflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //reactionVm.getReactions(reactionId)
    }

    /**
     *  Attempts to start video playback.
     *
     *  @param res Reaction resource item containing [ReactionItem.videoUri].
     */
    private fun onReactionLoaded(res: Resource<ReactionWrapperItem>) = with(res) {
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
