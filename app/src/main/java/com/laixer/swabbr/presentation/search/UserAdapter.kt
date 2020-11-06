package com.laixer.swabbr.presentation.search

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.utils.loadAvatar
import com.laixer.swabbr.presentation.model.UserItem
import kotlinx.android.synthetic.main.include_user_info.view.*

class UserAdapter(val context: Context, val onClick: (UserItem) -> Unit) :
    ListAdapter<UserItem, UserAdapter.ViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_user)) {

        fun bind(item: UserItem) {
            itemView.user_avatar.loadAvatar(item.profileImage, item.id)
            itemView.user_nickname.text = context.getString(R.string.nickname, item.nickname)

            item.firstName?.let {
                itemView.user_username.text = context.getString(R.string.full_name, it, item.lastName)
                itemView.user_username.visibility = View.VISIBLE
            }

            itemView.setOnClickListener { onClick.invoke(item) }
        }
    }
}

private class UserDiffCallback : DiffUtil.ItemCallback<UserItem>() {
    override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean = oldItem == newItem
}
