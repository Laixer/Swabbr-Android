package com.laixer.swabbr.presentation.vlogs.details

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import kotlinx.android.synthetic.main.item_list_reaction.view.*

class ReactionsAdapter(
    val onProfileClick: (ReactionWrapperItem) -> Unit,
    val onReactionClick: (ReactionWrapperItem) -> Unit
) :
    ListAdapter<ReactionWrapperItem, ReactionsAdapter.ViewHolder>(ReactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_reaction)) {

        // TODO Repair
        fun bind(item: ReactionWrapperItem) = with(itemView) {
            //user_avatar.loadAvatar(item.user.profileImage, item.user.id)

            item.user.firstName?.let {
//                user_username.apply {
//                    text = context.getString(R.string.full_name, it, item.user.lastName)
//                    visibility = View.VISIBLE
//                }
            }
            //user_nickname.text = context.getString(R.string.nickname, item.user.nickname)
            reactionPostDate.text = context.getString(
                R.string.date,
                item.reaction.dateCreated.dayOfMonth,
                item.reaction.dateCreated.monthValue,
                item.reaction.dateCreated.year
            )

            //user_avatar.setOnClickListener { onProfileClick.invoke(item) }
            itemView.setOnClickListener { onReactionClick.invoke(item) }
        }
    }
}

private class ReactionDiffCallback : DiffUtil.ItemCallback<ReactionWrapperItem>() {

    override fun areItemsTheSame(oldItem: ReactionWrapperItem, newItem: ReactionWrapperItem): Boolean =
        oldItem.reaction.id == newItem.reaction.id

    override fun areContentsTheSame(oldItem: ReactionWrapperItem, newItem: ReactionWrapperItem): Boolean =
        oldItem == newItem
}
