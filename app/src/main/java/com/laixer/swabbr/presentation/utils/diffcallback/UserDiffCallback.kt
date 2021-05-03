package com.laixer.swabbr.presentation.utils.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.laixer.swabbr.presentation.model.UserItem

// TODO This can be cleaned up.
/**
 *  Used to determine difference between items in a list.
 */
internal class UserDiffCallback : DiffUtil.ItemCallback<UserItem>() {
    override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean =
        oldItem == newItem
}
