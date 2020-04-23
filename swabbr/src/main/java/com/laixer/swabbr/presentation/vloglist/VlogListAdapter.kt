package com.laixer.swabbr.presentation.vloglist

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.loadAvatar
import com.laixer.swabbr.presentation.model.UserVlogItem
import kotlinx.android.synthetic.main.include_user_info.view.*
import kotlinx.android.synthetic.main.item_list_vlog.view.*

class VlogListAdapter constructor(
    private val context: Context,
    private val itemClick: (UserVlogItem) -> Unit,
    private val profileClick: (UserVlogItem) -> Unit
) : ListAdapter<UserVlogItem, VlogListAdapter.ViewHolder>(VlogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_vlog)) {

        fun bind(item: UserVlogItem) {
            val url = item.url.toString().replace("http:", "https:")

            Glide.with(context)
                .load(url)
                .thumbnail(THUMBNAIL_SIZE)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .dontTransform()
                .into(itemView.thumbnail)

            itemView.userAvatar.loadAvatar(item.profileImageUrl)
            itemView.userUsername.text = context.getString(R.string.nickname, item.nickname)
            itemView.userName.text = context.getString(R.string.full_name, item.firstName, item.lastName)
            itemView.vlogPostDate.text =
                context.getString(
                    R.string.date, item.dateStarted
                        .dayOfMonth, item.dateStarted.monthValue, item.dateStarted.year
                )

            itemView.vlogDuration.text = context.getString(R.string.duration, 0, 0, 0)
            itemView.reaction_count.text =
                context.getString(
                    R.string.reaction_count, item
                        .totalReactions
                )

            itemView.view_count.text =
                context.getString(
                    R.string.view_count, item
                        .totalViews
                )

            itemView.like_count.text =
                context.getString(
                    R.string.like_count, item
                        .totalLikes
                )

            itemView.userAvatar.setOnClickListener { profileClick.invoke(item) }
            itemView.userUsername.text = context.getString(R.string.nickname, item.nickname)
            itemView.userName.text = context.getString(R.string.full_name, item.firstName, item.lastName)

            itemView.setOnClickListener { itemClick.invoke(item) }
        }
    }

    companion object {
        private const val THUMBNAIL_SIZE = 0.1f
    }
}

private class VlogDiffCallback : DiffUtil.ItemCallback<UserVlogItem>() {
    override fun areItemsTheSame(oldItem: UserVlogItem, newItem: UserVlogItem): Boolean =
        oldItem.vlogId == newItem.vlogId

    override fun areContentsTheSame(oldItem: UserVlogItem, newItem: UserVlogItem): Boolean = oldItem == newItem
}
