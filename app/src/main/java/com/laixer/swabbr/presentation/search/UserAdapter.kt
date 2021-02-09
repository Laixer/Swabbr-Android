package com.laixer.swabbr.presentation.search

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.utils.loadAvatar
import kotlinx.android.synthetic.main.include_user_image_username_large.view.*
import kotlinx.android.synthetic.main.include_user_username.view.*

/**
 *  Adapter to display a list of [UserItem] objects.
 *
 *  @param context The application context.
 *  @param onClick Callback for when an item is clicked.
 */
class UserAdapter(
    val context: Context,
    val onClick: (UserItem) -> Unit
) :
    ListAdapter<UserItem, UserAdapter.ViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    /**
     *  Actual binding class for a [UserItem].
     */
    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_user)) {
        /**
         *  Method that specifies the binding for a [UserItem].
         */
        fun bind(item: UserItem) {
            itemView.user_profile_image.loadAvatar(item.profileImage, item.id)
            itemView.user_displayed_name.text = item.getDisplayName()
            itemView.user_nickname.text = context.getString(R.string.nickname, item.nickname)

            itemView.setOnClickListener { onClick.invoke(item) }
        }
    }
}

// TODO Duplicate functionality with LikingUserAdapter
// TODO This can be cleaned up.
private class UserDiffCallback : DiffUtil.ItemCallback<UserItem>() {
    override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean = oldItem == newItem
}
