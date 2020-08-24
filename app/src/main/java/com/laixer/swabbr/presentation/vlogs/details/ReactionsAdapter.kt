package com.laixer.swabbr.presentation.vlogs.details

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.ReactionItem
import kotlinx.android.synthetic.main.include_user_info.view.*
import kotlinx.android.synthetic.main.item_list_reaction.view.*

class ReactionsAdapter() :
    ListAdapter<ReactionItem, ReactionsAdapter.ViewHolder>(ReactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_reaction)) {

        fun bind(item: ReactionItem) = with(itemView) {
            user_avatar.loadAvatar(item.profileImage, item.userId)

            item.firstname?.let {
                user_username.apply {
                    text = context.getString(R.string.full_name, it, item.lastname)
                    visibility = View.VISIBLE
                }
            }
            user_nickname.text = context.getString(R.string.nickname, item.nickname)
            reactionPostDate.text = context.getString(
                R.string.date,
                item.datePosted.dayOfMonth,
                item.datePosted.monthValue,
                item.datePosted.year
            )
        }
    }
}

private class ReactionDiffCallback : DiffUtil.ItemCallback<ReactionItem>() {

    override fun areItemsTheSame(oldItem: ReactionItem, newItem: ReactionItem): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ReactionItem, newItem: ReactionItem): Boolean = oldItem == newItem
}
