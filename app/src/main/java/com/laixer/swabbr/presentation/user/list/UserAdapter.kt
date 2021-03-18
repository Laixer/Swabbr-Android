package com.laixer.swabbr.presentation.user.list

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.utils.diffcallback.UserDiffCallback
import com.laixer.swabbr.utils.loadAvatar
import kotlinx.android.synthetic.main.include_user_large.view.*
import kotlinx.android.synthetic.main.include_usernames.view.*

/**
 *  Adapter to display a list of [UserItem] objects.
 *
 *  @param context The application context.
 *  @param onClick Callback for when a list item is clicked.
 */
class UserAdapter(
    val context: Context,
    val onClick: (UserItem) -> Unit
) : ListAdapter<UserItem, UserAdapter.ViewHolder>(UserDiffCallback()) {

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
            itemView.user_nickname.text = context.getString(R.string.nickname, item.nickname)

            itemView.setOnClickListener { onClick.invoke(item) }
        }
    }
}
