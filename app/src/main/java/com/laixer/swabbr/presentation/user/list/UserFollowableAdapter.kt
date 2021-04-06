package com.laixer.swabbr.presentation.user.list

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import com.laixer.swabbr.presentation.utils.todosortme.gone
import com.laixer.swabbr.presentation.utils.todosortme.visible
import kotlinx.android.synthetic.main.item_list_user_followable.view.*
import java.util.*

/**
 *  Adapter for a [UserWithRelationItem] in a list which displays a follow
 *  button next to said user using [R.layout.item_list_user_followable].
 *
 *  @param context The application context.
 *  @param onClickProfile Callback for when we click the profile of this item.
 *  @param onClickFollow Callback for when we click the follow button. Note that
 *                       this can also be an unfollow, cancel or other operation.
 *  @param currentUserId Optional user id for the current user. Can be null, then
 *                       this parameter is ignored.
 */
class UserFollowableAdapter(
    context: Context,
    onClickProfile: (UserWithRelationItem) -> Unit,
    val onClickFollow: (UserWithRelationItem) -> Unit,
    private val currentUserId: UUID? // TODO Beun
) : UserWithRelationAdapter(
    context = context,
    layout = R.layout.item_list_user_followable,
    onClickProfile = onClickProfile
) {
    /**
     *  Apply the follow button functionality to each item.
     */
    override fun bindAlso(itemView: View, item: UserWithRelationItem) {
        // Hide the follow button if it's us.
        if (currentUserId != null && currentUserId == item.user.id) {
            itemView.user_follow_button.gone()
        } else {
            itemView.user_follow_button.visible()
            itemView.user_follow_button.setOnClickListener { onClickFollow.invoke(item) }
        }

        // Control the follow button according to the follow request status.
        when (item.followRequestStatus) {
            FollowRequestStatus.ACCEPTED -> itemView.user_follow_button.text = context.getString(R.string.follow_request_accepted)
            FollowRequestStatus.DECLINED -> itemView.user_follow_button.text = context.getString(R.string.follow_request_follow)
            FollowRequestStatus.PENDING -> itemView.user_follow_button.text = context.getString(R.string.follow_request_requested)
            FollowRequestStatus.NONEXISTENT -> itemView.user_follow_button.text = context.getString(R.string.follow_request_follow)
        }
    }
}

