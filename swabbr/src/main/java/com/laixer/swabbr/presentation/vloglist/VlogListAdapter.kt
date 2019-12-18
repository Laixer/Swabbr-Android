package com.laixer.swabbr.presentation.vloglist

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.presentation.inflate
import com.laixer.swabbr.presentation.model.ProfileItem
import com.laixer.swabbr.presentation.model.VlogItem
import kotlinx.android.synthetic.main.include_user_info.view.*
import kotlinx.android.synthetic.main.item_list_vlog.view.*

class VlogListAdapter constructor(
    private val context: Context,
    private val itemClick: (Pair<ProfileItem, VlogItem>) -> Unit,
    private val profileClick: (Pair<ProfileItem, VlogItem>) -> Unit
) :
    ListAdapter<Pair<ProfileItem, VlogItem>, VlogListAdapter.ViewHolder>(VlogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_vlog)) {

        fun bind(item: Pair<ProfileItem, VlogItem>) {
            itemView.userAvatar.loadAvatar(item.first.id)
            itemView.userAvatar.setOnClickListener { profileClick.invoke(item) }
            itemView.userUsername.text = context.getString(R.string.nickname, item.first.nickname)
            itemView.userName.text = context.getString(R.string.full_name, item.first.firstName, item.first.lastName)
            itemView.vlogDuration.text = item.second.duration
            itemView.vlogPostDate.text = item.second.startDate
            itemView.setOnClickListener { itemClick.invoke(item) }
        }
    }
}

private class VlogDiffCallback : DiffUtil.ItemCallback<Pair<ProfileItem, VlogItem>>() {
    override fun areItemsTheSame(
        oldItem: Pair<ProfileItem, VlogItem>,
        newItem: Pair<ProfileItem, VlogItem>
    ): Boolean =
        oldItem.second.vlogId == newItem.second.vlogId

    override fun areContentsTheSame(
        oldItem: Pair<ProfileItem, VlogItem>,
        newItem: Pair<ProfileItem, VlogItem>
    ): Boolean =
        oldItem == newItem
}
