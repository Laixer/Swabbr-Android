package com.laixer.swabbr.presentation.utils.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.laixer.swabbr.presentation.model.ReactionWrapperItem

/**
 *  Difference callback for [ReactionWrapperItem] lists.
 */
internal class ReactionWrapperDiffCallback : DiffUtil.ItemCallback<ReactionWrapperItem>() {
    override fun areItemsTheSame(oldItem: ReactionWrapperItem, newItem: ReactionWrapperItem): Boolean =
        oldItem.reaction.id == newItem.reaction.id

    override fun areContentsTheSame(oldItem: ReactionWrapperItem, newItem: ReactionWrapperItem): Boolean =
        oldItem.reaction.id == newItem.reaction.id
}
