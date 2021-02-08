package com.laixer.swabbr.presentation.reaction

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.utils.loadAvatar
import kotlinx.android.synthetic.main.item_list_reaction.view.*

/**
 *  Adapter for reaction display.
 *
 *  @param onProfileClick Callback for when we click [reaction_user_profile_image].
 *  @param onReactionClick Callback for when we click the [ReactionWrapperItem] item.
 */
class ReactionsAdapter(
    val onProfileClick: (ReactionWrapperItem) -> Unit,
    val onReactionClick: (ReactionWrapperItem) -> Unit
) :
    ListAdapter<ReactionWrapperItem, ReactionsAdapter.ViewHolder>(ReactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_reaction)) {

        /**
         *  Binds a single [ReactionWrapperItem] to the UI.
         */
        fun bind(item: ReactionWrapperItem) = with(itemView) {
            reaction_user_profile_image.loadAvatar(item.user.profileImage, item.user.id)
            reaction_user_displayed_name.text = item.user.getDisplayName()
            reaction_user_nickname.text = context.getString(R.string.nickname, item.user.nickname)

            // Take us to the user if we click the profile image.
            reaction_user_profile_image.setOnClickListener { onProfileClick.invoke(item) }

            // Take us to the reaction if we click the item.
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
