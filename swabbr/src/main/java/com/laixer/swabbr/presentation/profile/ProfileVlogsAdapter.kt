package com.laixer.swabbr.presentation.profile

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.VlogItem
import kotlinx.android.synthetic.main.item_list_profilevlog.view.like_count
import kotlinx.android.synthetic.main.item_list_profilevlog.view.thumbnail
import kotlinx.android.synthetic.main.item_list_profilevlog.view.view_count
import kotlinx.android.synthetic.main.item_list_profilevlog.view.vlogDuration
import kotlinx.android.synthetic.main.item_list_profilevlog.view.vlogPostDate
import kotlinx.android.synthetic.main.item_list_vlog.view.*

class ProfileVlogsAdapter(
    private val context: Context, private val onClick: (VlogItem)
    -> Unit
) :
    ListAdapter<VlogItem, ProfileVlogsAdapter.ViewHolder>(ProfileDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_profilevlog)) {

        fun bind(item: VlogItem) {
            val url = item.url.toString().replace("http:", "https:")

            Glide.with(context)
                .load(url)
                .into(itemView.thumbnail)

            itemView.vlogPostDate.text =
                context.getString(
                    R.string.date, item.dateStarted.dayOfMonth, item.dateStarted.monthValue, item.dateStarted.year
                )
            itemView.vlogDuration.text = context.getString(R.string.duration, 0, 0, 0)
            itemView.reaction_count.text =
                context.getString(
                    R.string.reaction_count, 0
                )

            itemView.view_count.text =
                context.getString(
                    R.string.view_count, 0
                )

            itemView.like_count.text =
                context.getString(
                    R.string.like_count, 0
                )
            itemView.setOnClickListener { onClick.invoke(item) }
        }
    }
}

private class ProfileDiffCallback : DiffUtil.ItemCallback<VlogItem>() {
    override fun areItemsTheSame(oldItem: VlogItem, newItem: VlogItem): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: VlogItem, newItem: VlogItem): Boolean = oldItem == newItem
}
