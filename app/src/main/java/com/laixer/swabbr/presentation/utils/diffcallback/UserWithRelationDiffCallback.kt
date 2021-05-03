package com.laixer.swabbr.presentation.utils.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.laixer.swabbr.presentation.model.UserWithRelationItem

// TODO Clean up.
/**
 *  Used to determine difference between two [UserWithRelationItem]s.
 */
internal class UserWithRelationDiffCallback : DiffUtil.ItemCallback<UserWithRelationItem>() {
    override fun areItemsTheSame(oldItem: UserWithRelationItem, newItem: UserWithRelationItem): Boolean =
        oldItem.user.id == newItem.user.id

    override fun areContentsTheSame(oldItem: UserWithRelationItem, newItem: UserWithRelationItem): Boolean =
        oldItem.user.id == newItem.user.id &&
            oldItem.followRequestStatus == newItem.followRequestStatus
}
