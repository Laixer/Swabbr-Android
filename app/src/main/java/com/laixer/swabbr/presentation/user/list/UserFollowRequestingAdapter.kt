package com.laixer.swabbr.presentation.user.list

import android.content.Context
import android.view.View
import com.laixer.swabbr.presentation.utils.todosortme.gone
import com.laixer.swabbr.presentation.utils.todosortme.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import kotlinx.android.synthetic.main.item_list_user_acceptdeny.view.*

/**
 *  Adapter for a [UserWithRelationItem] in a list which displays
 *  an accept and decline button for follow requests using
 *  [R.layout.item_list_user_followable].
 *
 *  @param context The application context.
 *  @param onClickProfile Callback for when we click the profile of this item.
 *  @param onClickAccept Callback for when we click accept.
 *  @param onClickDecline Callback for when we click decline.
 */
class UserFollowRequestingAdapter(
    context: Context,
    onClickProfile: (UserWithRelationItem) -> Unit,
    val onClickAccept: (UserWithRelationItem) -> Unit,
    val onClickDecline: (UserWithRelationItem) -> Unit
) : UserWithRelationAdapter(
    context = context,
    layout = R.layout.item_list_user_acceptdeny,
    onClickProfile = onClickProfile
) {
    /**
     *  Add the accept and decline buttons to the item.
     */
    override fun bindAlso(itemView: View, item: UserWithRelationItem) {
        if (item.followRequestStatus == FollowRequestStatus.PENDING) {
            itemView.follow_request_accept.visible()
            itemView.follow_request_decline.visible()

            itemView.follow_request_accept.setOnClickListener { onClickAccept.invoke(item) }
            itemView.follow_request_decline.setOnClickListener { onClickDecline.invoke(item) }
        } else {
            // Hide if not required.
            itemView.follow_request_accept.gone()
            itemView.follow_request_decline.gone()
        }
    }
}
