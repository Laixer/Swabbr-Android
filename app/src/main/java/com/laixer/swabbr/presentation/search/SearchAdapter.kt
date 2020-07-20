package com.laixer.swabbr.presentation.search

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.UserItem
import kotlinx.android.synthetic.main.include_user_info.view.*

class SearchAdapter(val context: Context, val onClick: (UserItem) -> Unit) :
    ListAdapter<UserItem, SearchAdapter.ViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_user)) {

        fun bind(item: UserItem) {
            itemView.userAvatar.loadAvatar(item.profileImage, item.id)
            itemView.userUsername.text = context.getString(R.string.nickname, item.nickname)

            item.firstName?.let {
                itemView.userName.visibility = View.VISIBLE
                itemView.userName.text = context.getString(R.string.full_name, item.firstName, item.lastName)
            } ?: run {
                itemView.userName.visibility = View.GONE
            }
            itemView.setOnClickListener { onClick.invoke(item) }
        }
    }
}

private class ProfileDiffCallback : DiffUtil.ItemCallback<UserItem>() {
    override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean = oldItem == newItem
}
