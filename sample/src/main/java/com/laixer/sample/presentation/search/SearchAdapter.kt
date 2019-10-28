package com.laixer.sample.presentation.search

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.sample.R
import com.laixer.presentation.inflate
import com.laixer.sample.presentation.loadAvatar
import com.laixer.sample.presentation.model.ProfileItem
import kotlinx.android.synthetic.main.include_user_info.view.*

class SearchAdapter(val context: Context) : ListAdapter<ProfileItem, SearchAdapter.ViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_profile)) {

        fun bind(item: ProfileItem) {
            itemView.userAvatar.loadAvatar(item.id)
            itemView.userUsername.text = context.getString(R.string.nickname, item.nickname)
            itemView.userName.text = context.getString(R.string.full_name, item.firstName, item.lastName)
        }
    }
}

private class ProfileDiffCallback : DiffUtil.ItemCallback<ProfileItem>() {
    override fun areItemsTheSame(oldItem: ProfileItem, newItem: ProfileItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ProfileItem, newItem: ProfileItem): Boolean =
        oldItem == newItem
}
