package com.laixer.swabbr.presentation.profile

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.UserVlogItem
import kotlinx.android.synthetic.main.item_list_uservlog.view.like_count
import kotlinx.android.synthetic.main.item_list_uservlog.view.thumbnail
import kotlinx.android.synthetic.main.item_list_uservlog.view.view_count
import kotlinx.android.synthetic.main.item_list_uservlog.view.vlogDuration
import kotlinx.android.synthetic.main.item_list_uservlog.view.vlogPostDate
import kotlinx.android.synthetic.main.item_list_vlog.view.*
import java.time.ZonedDateTime

class ProfileVlogsAdapter(
    private val onClick: (UserVlogItem) -> Unit
) : ListAdapter<UserVlogItem, ProfileVlogsAdapter.ViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_uservlog)) {

        fun bind(item: UserVlogItem) = with(itemView) {
            if (item.vlog.data.dateStarted.isBefore(ZonedDateTime.now().minusMinutes(3))) {
                processing_cover.visibility = View.GONE

                /* We convert back to String because Glide's load(URL) function is deprecated because
                 of possible performance issues. */
                val url = item.url.toString()

                Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.1f)
                    .into(thumbnail)

                vlogPostDate.text =
                    context.getString(
                        R.string.date, item.vlog.data.dateStarted.dayOfMonth, item.vlog.data.dateStarted.monthValue, item.vlog.data.dateStarted.year
                    )
                vlogDuration.text =
                    context.getString(R.string.duration, (Math.random() * 10).toInt(), (Math.random() * 60).toInt())
                reaction_count.text = context.getString(R.string.reaction_count, (Math.random() * 100).toInt())

                view_count.text = context.getString(R.string.view_count, item.vlog.data.views * (Math.random() * 1000).toInt())
                like_count.text = context.getString(R.string.like_count, (Math.random() * 1000).toInt())
                setOnClickListener { onClick.invoke(item) }
            }
        }
    }
}

private class ProfileDiffCallback : DiffUtil.ItemCallback<UserVlogItem>() {

    override fun areItemsTheSame(oldItem: UserVlogItem, newItem: UserVlogItem): Boolean =
        oldItem.vlog.data.id == newItem.vlog.data.id

    override fun areContentsTheSame(oldItem: UserVlogItem, newItem: UserVlogItem): Boolean = oldItem == newItem
}
