package com.laixer.swabbr.presentation.user.list

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.swabbr.presentation.utils.todosortme.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import com.laixer.swabbr.presentation.utils.diffcallback.UserWithRelationDiffCallback
import com.laixer.swabbr.utils.loadAvatarFromUser
import kotlinx.android.synthetic.main.include_user_large.view.*
import kotlinx.android.synthetic.main.include_usernames.view.*

/**
 *  Adapter for a [UserWithRelationItem] in a list. This can be extended to
 *  add additional functionality to each item.
 *
 *  @param context The application context.
 *  @param layout The layout resource for each item, defaults to [R.layout.item_list_user].
 *  @param onClickProfile Callback for when we click the profile of this item.
 */
open class UserWithRelationAdapter(
    val context: Context,
    val layout: Int = R.layout.item_list_user,
    val onClickProfile: (UserWithRelationItem) -> Unit
) : ListAdapter<UserWithRelationItem, UserWithRelationAdapter.ViewHolder>(
    UserWithRelationDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    /**
     *  Actual binding class for each item.
     */
    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(layout)) {
        /**
         *  Binds a single [UserWithRelationItem].
         */
        fun bind(item: UserWithRelationItem) {
            itemView.user_profile_image_large.loadAvatarFromUser(item.user)
            itemView.user_nickname.text = context.getString(R.string.nickname, item.user.nickname)

            itemView.setOnClickListener { onClickProfile.invoke(item) }

            bindAlso(itemView, item)
        }
    }

    /**
     *  Override this method to apply additional functionality to each
     *  item in the list.
     *
     *  @param itemView The view in which we are inflating [layout].
     *  @param item The item which corresponds to this entry.
     */
    protected open fun bindAlso(itemView: View, item: UserWithRelationItem) { }
}

