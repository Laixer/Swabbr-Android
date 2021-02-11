package com.laixer.swabbr.presentation.likeoverview

import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.model.LikingUserWrapper
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.presentation.model.LikingUserWrapperItem
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.utils.loadAvatar
import kotlinx.android.synthetic.main.include_user_image_username_large.view.*
import kotlinx.android.synthetic.main.include_user_username.view.*
import kotlinx.android.synthetic.main.liking_user_list_item.view.*

/**
 *  Adapter for a [LikingUserWrapperItem] in a list.
 *
 *  @param context The application context.
 *  @param onProfileClick Callback for when we click the profile of this item.
 *  @param onFollowClick Callback for when we click the follow button.
 */
class LikingUserAdapter(
    val context: Context,
    val onProfileClick: (LikingUserWrapperItem) -> Unit,
    val onFollowClick: (LikingUserWrapperItem, Button) -> Unit
) :
    ListAdapter<LikingUserWrapperItem, LikingUserAdapter.ViewHolder>(LikingUserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    /**
     *  Actual binding class for each item.
     */
    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.liking_user_list_item)) {
        /**
         *  Binds a single [LikingUserWrapperItem].
         */
        fun bind(item: LikingUserWrapperItem) {
            itemView.user_profile_image.loadAvatar(item.vlogLikingUser.profileImage, item.vlogLikingUser.id)
            itemView.user_displayed_name.text = item.vlogLikingUser.getDisplayName()
            itemView.user_nickname.text = context.getString(R.string.nickname, item.vlogLikingUser.nickname)

            itemView.include_user_profile.setOnClickListener { onProfileClick.invoke(item) }
            itemView.user_follow_button.setOnClickListener { onFollowClick.invoke(item, itemView.user_follow_button) }

            // Control the follow button according to the follow request status.
            when (item.followRequestStatus) {
                FollowRequestStatus.ACCEPTED -> itemView.user_follow_button.isVisible = false
                FollowRequestStatus.DECLINED -> itemView.user_follow_button.isVisible = true
                FollowRequestStatus.PENDING -> itemView.user_follow_button.isVisible = false
                FollowRequestStatus.NONEXISTENT -> itemView.user_follow_button.isVisible = true
            }
        }
    }
}

// TODO Duplicate with UserAdapter.kt
// TODO This can be optimized a lot.
private class LikingUserDiffCallback : DiffUtil.ItemCallback<LikingUserWrapperItem>() {
    override fun areItemsTheSame(oldItem: LikingUserWrapperItem, newItem: LikingUserWrapperItem): Boolean =
            oldItem.vlogLikeEntity.vlogId == newItem.vlogLikeEntity.vlogId
            && oldItem.vlogLikeEntity.userId == newItem.vlogLikeEntity.userId

    override fun areContentsTheSame(oldItem: LikingUserWrapperItem, newItem: LikingUserWrapperItem): Boolean =
        oldItem == newItem
}
