package com.laixer.swabbr.presentation.user.list

import android.content.Context
import android.view.View
import com.laixer.swabbr.R
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
 *  @param onClickVlog Callback for when we click the vlog button.
 *  @param currentUserId Optional user id for the current user. Can be null, then
 *                       this parameter is ignored.
 */
class UserWithVlogAdapter(
    context: Context,
    onClickProfile: (UserWithRelationItem) -> Unit,
    val onClickVlog: (UserWithRelationItem) -> Unit, // TODO Duplicate with UserFollowableAdapter
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
            itemView.user_follow_button.setOnClickListener { onClickVlog.invoke(item) }
        }

        itemView.user_follow_button.text = "Vlog" // TODO Hard coded
    }
}

