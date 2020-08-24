package com.laixer.swabbr.presentation.vlogs.list

import android.content.Context
import android.view.View
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
    private val itemClick: (UserVlogItem) -> Unit,
    private val profileClick: (UserVlogItem) -> Unit
) : ListAdapter<UserVlogItem, VlogListAdapter.ViewHolder>(VlogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_vlog)) {

        fun bind(item: UserVlogItem) = with(itemView) {
            val url = item.url.toString().replace("http:", "https:")

            Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.1f)
                .into(thumbnail)


            user_avatar.loadAvatar(item.profileImage, item.userId)
            user_nickname.text = context.getString(R.string.nickname, item.nickname)
            item.firstName?.let {
                user_username.apply {
                    text = context.getString(R.string.full_name, it, item.lastName)
                    visibility = View.VISIBLE
                }
            }

            vlogPostDate.text =
                context.getString(
                    R.string.date, item.dateStarted.dayOfMonth, item.dateStarted.monthValue, item.dateStarted.year
                )

            vlogDuration.text = context.getString(R.string.duration, 0, 0, 0)
            reaction_count.text =
                context.getString(
                    R.string.reaction_count, 0
                )

            view_count.text =
                context.getString(
                    R.string.view_count, 0
                )

            like_count.text = context.getString(R.string.like_count, 0)

            user_avatar.setOnClickListener { profileClick.invoke(item) }
            user_nickname.text = context.getString(R.string.nickname, item.nickname)

            item.firstName?.let {
                itemView.user_username.apply {
                    text = context.getString(R.string.full_name, it, item.lastName)
                    visibility = View.VISIBLE
                }
            }

            setOnClickListener { itemClick.invoke(item) }
        }
    }

    companion object {
        private const val THUMBNAIL_SIZE = 1f
    }
}

private class VlogDiffCallback : DiffUtil.ItemCallback<UserVlogItem>() {

    override fun areItemsTheSame(oldItem: UserVlogItem, newItem: UserVlogItem): Boolean =
        oldItem.vlogId == newItem.vlogId

    override fun areContentsTheSame(oldItem: UserVlogItem, newItem: UserVlogItem): Boolean = oldItem == newItem
}
