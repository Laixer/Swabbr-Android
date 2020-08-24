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
import kotlinx.android.synthetic.main.item_list_uservlog.view.like_count
import kotlinx.android.synthetic.main.item_list_uservlog.view.thumbnail
import kotlinx.android.synthetic.main.item_list_uservlog.view.view_count
import kotlinx.android.synthetic.main.item_list_uservlog.view.vlogDuration
import kotlinx.android.synthetic.main.item_list_uservlog.view.vlogPostDate
import kotlinx.android.synthetic.main.item_list_vlog.view.*

class ProfileVlogsAdapter(private val onClick: (UserVlogItem) -> Unit
) : ListAdapter<UserVlogItem, ProfileVlogsAdapter.ViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_uservlog)) {

        fun bind(item: UserVlogItem) = with(itemView) {
            /* We convert back to String because Glide's load(URL) function is deprecated because
             of possible performance issues. */
            val url = item.url.toString()

            Glide.with(context)
                .load(url)
                .into(thumbnail)

            vlogPostDate.text =
                context.getString(
                    R.string.date, item.dateStarted.dayOfMonth, item.dateStarted.monthValue, item.dateStarted.year
                )
            vlogDuration.text = context.getString(R.string.duration, 0, 0, 0)
            reaction_count.text = context.getString(R.string.reaction_count, 0)

            view_count.text = context.getString(R.string.view_count, 0)
            like_count.text = context.getString(R.string.like_count, 0)
            setOnClickListener { onClick.invoke(item) }
        }
    }
}

private class ProfileDiffCallback : DiffUtil.ItemCallback<UserVlogItem>() {

    override fun areItemsTheSame(oldItem: UserVlogItem, newItem: UserVlogItem): Boolean =
        oldItem.vlogId == newItem.vlogId

    override fun areContentsTheSame(oldItem: UserVlogItem, newItem: UserVlogItem): Boolean = oldItem == newItem
}
