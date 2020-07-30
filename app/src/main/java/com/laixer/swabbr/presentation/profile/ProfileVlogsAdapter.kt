package com.laixer.swabbr.presentation.profile

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.UserVlogItem
import com.laixer.swabbr.presentation.model.VlogItem
import kotlinx.android.synthetic.main.item_list_uservlog.view.like_count
import kotlinx.android.synthetic.main.item_list_uservlog.view.thumbnail
import kotlinx.android.synthetic.main.item_list_uservlog.view.view_count
import kotlinx.android.synthetic.main.item_list_uservlog.view.vlogDuration
import kotlinx.android.synthetic.main.item_list_uservlog.view.vlogPostDate
import kotlinx.android.synthetic.main.item_list_vlog.view.*

class ProfileVlogsAdapter(
    private val context: Context, private val onClick: (UserVlogItem) -> Unit
) : ListAdapter<UserVlogItem, ProfileVlogsAdapter.ViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_uservlog)) {

        fun bind(item: UserVlogItem) {
            val url = item.url
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

private class ProfileDiffCallback : DiffUtil.ItemCallback<UserVlogItem>() {
    override fun areItemsTheSame(oldItem: UserVlogItem, newItem: UserVlogItem): Boolean = oldItem.vlogId == newItem.vlogId

    override fun areContentsTheSame(oldItem: UserVlogItem, newItem: UserVlogItem): Boolean = oldItem == newItem
}
