package com.laixer.swabbr.presentation.profile

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.swabbr.R
import com.laixer.presentation.inflate
import com.laixer.swabbr.presentation.model.VlogItem
import kotlinx.android.synthetic.main.item_list_profilevlog.view.*

class ProfileVlogsAdapter : ListAdapter<VlogItem, ProfileVlogsAdapter.ViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_profilevlog)) {

        fun bind(item: VlogItem) {
            itemView.vlogPostDate.text = item.startDate
            itemView.vlogDuration.text = item.duration
        }
    }
}

private class ProfileDiffCallback : DiffUtil.ItemCallback<VlogItem>() {
    override fun areItemsTheSame(oldItem: VlogItem, newItem: VlogItem): Boolean =
        oldItem.vlogId == newItem.vlogId

    override fun areContentsTheSame(oldItem: VlogItem, newItem: VlogItem): Boolean =
        oldItem == newItem
}
