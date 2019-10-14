package com.laixer.sample.presentation.profile

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laixer.sample.R
import com.laixer.presentation.inflate
import com.laixer.sample.presentation.model.VlogItem
import kotlinx.android.synthetic.main.item_list_profilevlog.view.*

class ProfileAdapter : ListAdapter<VlogItem, ProfileAdapter.ViewHolder>(ProfileDiffCallback()) {

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
