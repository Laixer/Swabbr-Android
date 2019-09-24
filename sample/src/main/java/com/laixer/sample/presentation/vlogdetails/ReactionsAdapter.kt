package com.laixer.sample.presentation.vlogdetails

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.sample.R
import com.laixer.sample.presentation.loadAvatar
import com.laixer.presentation.inflate
import com.laixer.sample.presentation.model.ReactionItem
import kotlinx.android.synthetic.main.include_user_info_small.view.*
import kotlinx.android.synthetic.main.item_list_reaction.view.*

class ReactionsAdapter : ListAdapter<ReactionItem, ReactionsAdapter.ViewHolder>(ReactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_reaction)) {

        fun bind(item: ReactionItem) {
            itemView.userAvatar.loadAvatar(item.userId)
            itemView.userName.text = "@${item.nickname}"
            itemView.reactionPostDate.text = item.postDate
        }
    }
}

private class ReactionDiffCallback : DiffUtil.ItemCallback<ReactionItem>() {
    override fun areItemsTheSame(oldItem: ReactionItem, newItem: ReactionItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ReactionItem, newItem: ReactionItem): Boolean =
        oldItem == newItem
}
