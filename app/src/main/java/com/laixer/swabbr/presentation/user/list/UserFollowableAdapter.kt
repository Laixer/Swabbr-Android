package com.laixer.swabbr.presentation.user.list

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import kotlinx.android.synthetic.main.item_list_user_followable.view.*

/**
 *  Adapter for a [UserWithRelationItem] in a list which displays a follow
 *  button next to said user using [R.layout.item_list_user_followable].
 *
 *  @param context The application context.
 *  @param onClickProfile Callback for when we click the profile of this item.
 *  @param onClickFollow Callback for when we click the follow button.
 */
class UserFollowableAdapter(
    context: Context,
    onClickProfile: (UserWithRelationItem) -> Unit,
    val onClickFollow: (UserWithRelationItem) -> Unit
) : UserWithRelationAdapter(
    context = context,
    layout = R.layout.item_list_user_followable,
    onClickProfile = onClickProfile
) {
    /**
     *  Apply the follow button functionality to each item.
     */
    override fun bindAlso(itemView: View, item: UserWithRelationItem) {
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

