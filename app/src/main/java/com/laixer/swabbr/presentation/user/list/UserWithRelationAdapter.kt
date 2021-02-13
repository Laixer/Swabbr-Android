package com.laixer.swabbr.presentation.user.list

import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import com.laixer.swabbr.presentation.utils.diffcallback.UserWithRelationDiffCallback
import com.laixer.swabbr.utils.loadAvatar
import kotlinx.android.synthetic.main.include_user_large.view.*
import kotlinx.android.synthetic.main.include_usernames.view.*
import kotlinx.android.synthetic.main.item_list_user_followable.view.*

/**
 *  Adapter for a [UserWithRelationItem] in a list.
 *
 *  @param context The application context.
 *  @param onClickProfile Callback for when we click the profile of this item.
 *  @param onClickFollow Callback for when we click the follow button.
 */
class UserWithRelationAdapter(
    val context: Context,
    val onClickProfile: (UserWithRelationItem) -> Unit,
    val onClickFollow: (UserWithRelationItem) -> Unit
) : ListAdapter<UserWithRelationItem, UserWithRelationAdapter.ViewHolder>(
    UserWithRelationDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    /**
     *  Actual binding class for each item.
     */
    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_user_followable)) {
        /**
         *  Binds a single [UserWithRelationItem].
         */
        fun bind(item: UserWithRelationItem) {
            itemView.user_profile_image.loadAvatar(item.user.profileImage, item.user.id)
            itemView.user_displayed_name.text = item.user.getDisplayName()
            itemView.user_nickname.text = context.getString(R.string.nickname, item.user.nickname)

            itemView.setOnClickListener { onClickProfile.invoke(item) }
            itemView.user_follow_button.setOnClickListener { onClickFollow.invoke(item) }

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

