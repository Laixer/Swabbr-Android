package com.laixer.swabbr.presentation.reaction.playback

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.swabbr.presentation.utils.todosortme.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.utils.diffcallback.ReactionWrapperDiffCallback
import com.laixer.swabbr.utils.loadAvatar
import kotlinx.android.synthetic.main.item_list_reaction.view.*
import java.util.*

/**
 *  Adapter for reaction display.
 *
 *  @param currentUserId The currently logged in user. TODO This should be refactored!
 *  @param onProfileClick Callback for when we click [user_profile_image].
 *  @param onReactionClick Callback for when we click the [ReactionWrapperItem] item.
 *  @param onDeleteClick Callback for when we click [button_reaction_delete]. Note
 *                       that this is nullable.
 */
class ReactionsAdapter(
    val currentUserId: UUID,
    val onProfileClick: (UserItem) -> Unit,
    val onReactionClick: (ReactionWrapperItem) -> Unit,
    val onDeleteClick: (ReactionWrapperItem) -> Unit?
) :
    ListAdapter<ReactionWrapperItem, ReactionsAdapter.ViewHolder>(ReactionWrapperDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_reaction)) {

        /**
         *  Binds a single [ReactionWrapperItem] to the UI.
         */
        fun bind(item: ReactionWrapperItem) = with(itemView) {
            user_profile_image.loadAvatar(item.user.profileImage, item.user.id)
            video_user_nickname.text = context.getString(R.string.nickname, item.user.nickname)

            // Take us to the user if we click the profile image.
            user_profile_image.setOnClickListener { onProfileClick.invoke(item.user) }

            // Take us to the reaction if we click the item.
            itemView.setOnClickListener { onReactionClick.invoke(item) }

            // Delete the reaction if we own the reaction, else hide the delete button.
            if (item.user.id == currentUserId) {
                button_reaction_delete.isVisible = true
                button_reaction_delete.isEnabled = true
                button_reaction_delete.setOnClickListener { onDeleteClick.invoke(item) }
            } else {
                button_reaction_delete.isVisible = false
                button_reaction_delete.isEnabled = false
            }
        }
    }
}
