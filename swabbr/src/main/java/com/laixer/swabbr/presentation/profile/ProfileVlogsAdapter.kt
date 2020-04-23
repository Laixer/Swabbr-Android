package com.laixer.swabbr.presentation.profile

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.VlogItem
import kotlinx.android.synthetic.main.item_list_profilevlog.view.*

class ProfileVlogsAdapter(private val context: Context, private val onClick: (VlogItem) -> Unit) :
    ListAdapter<VlogItem, ProfileVlogsAdapter.ViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_profilevlog)) {

        fun bind(item: VlogItem) {
            val url = item.url.toString().replace("http:", "https:")

            Glide.with(context).load(url).placeholder(R.drawable.thumbnail_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().dontTransform().into(itemView.thumbnail)
            itemView.vlogPostDate.text = item.dateStarted.toString()
            itemView.setOnClickListener { onClick.invoke(item) }
        }
    }
}

private class ProfileDiffCallback : DiffUtil.ItemCallback<VlogItem>() {
    override fun areItemsTheSame(oldItem: VlogItem, newItem: VlogItem): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: VlogItem, newItem: VlogItem): Boolean = oldItem == newItem
}
