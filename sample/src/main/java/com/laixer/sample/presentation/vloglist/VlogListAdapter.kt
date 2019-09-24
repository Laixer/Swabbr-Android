package com.laixer.sample.presentation.vloglist

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.sample.R
import com.laixer.sample.presentation.loadAvatar
import com.laixer.presentation.inflate
import com.laixer.sample.presentation.model.VlogItem
import kotlinx.android.synthetic.main.include_user_info.view.*
import kotlinx.android.synthetic.main.item_list_vlog.view.*

class VlogListAdapter constructor(private val itemClick: (VlogItem) -> Unit) :
    ListAdapter<VlogItem, VlogListAdapter.ViewHolder>(VlogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_vlog)) {

        fun bind(item: VlogItem) {
            itemView.userAvatar.loadAvatar(item.userId)
            itemView.userUsername.text = "@${item.nickname}"
            itemView.userName.text = "${item.firstName} ${item.lastName}"
            itemView.vlogDuration.text = item.duration
            itemView.vlogPostDate.text = item.startDate
            itemView.setOnClickListener { itemClick.invoke(item) }
        }
    }
}

private class VlogDiffCallback : DiffUtil.ItemCallback<VlogItem>() {
    override fun areItemsTheSame(oldItem: VlogItem, newItem: VlogItem): Boolean =
        oldItem.vlogId == newItem.vlogId

    override fun areContentsTheSame(oldItem: VlogItem, newItem: VlogItem): Boolean =
        oldItem == newItem
}
