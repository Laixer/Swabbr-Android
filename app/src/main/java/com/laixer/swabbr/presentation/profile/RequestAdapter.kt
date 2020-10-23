package com.laixer.swabbr.presentation.profile

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.FollowRequestItem
import com.laixer.swabbr.presentation.model.UserItem
import kotlinx.android.synthetic.main.include_follow_request.view.*
import kotlinx.android.synthetic.main.include_user_info.view.*

class RequestAdapter(
    val context: Context,
    val onProfileClick: (Pair<FollowRequestItem, UserItem>) -> Unit,
    val onAccept: (Pair<FollowRequestItem, UserItem>) -> Unit,
    val onDecline: (Pair<FollowRequestItem, UserItem>) -> Unit
) : ListAdapter<Pair<FollowRequestItem, UserItem>, RequestAdapter.ViewHolder>(RequestDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.include_follow_request)) {

        fun bind(item: Pair<FollowRequestItem, UserItem>) {
            val request = item.first
            val user = item.second

            itemView.user_avatar.loadAvatar(user.profileImage, user.id)
            itemView.user_nickname.text = context.getString(R.string.nickname, user.nickname)

            user.firstName?.let {
                itemView.user_username.text = context.getString(R.string.full_name, it, user.lastName)
                itemView.user_username.visibility = View.VISIBLE
            }

            itemView.followrequest_accept.setOnClickListener { onAccept.invoke(item) }
            itemView.followrequest_decline.setOnClickListener { onDecline.invoke(item) }
            itemView.followrequest_user_info.setOnClickListener { onProfileClick.invoke(item) }
        }
    }
}

private class RequestDiffCallback : DiffUtil.ItemCallback<Pair<FollowRequestItem, UserItem>>() {
    override fun areItemsTheSame(
        oldItem: Pair<FollowRequestItem, UserItem>,
        newItem: Pair<FollowRequestItem, UserItem>
    ): Boolean = oldItem.second.id == newItem.second.id

    override fun areContentsTheSame(
        oldItem: Pair<FollowRequestItem, UserItem>,
        newItem: Pair<FollowRequestItem, UserItem>
    ): Boolean = oldItem.first.status == newItem.first.status
}
