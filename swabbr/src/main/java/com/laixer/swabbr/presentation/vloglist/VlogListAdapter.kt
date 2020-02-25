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
import com.laixer.swabbr.presentation.model.ProfileVlogItem
import kotlinx.android.synthetic.main.include_user_info.view.*
import kotlinx.android.synthetic.main.item_list_vlog.view.*

class VlogListAdapter constructor(
    private val context: Context,
    private val itemClick: (ProfileVlogItem) -> Unit,
    private val profileClick: (ProfileVlogItem) -> Unit
) :
    ListAdapter<ProfileVlogItem, VlogListAdapter.ViewHolder>(VlogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_vlog)) {

        fun bind(item: ProfileVlogItem) {
            val url = item.url.replace("http://", "https://")

            Glide.with(context)
                .load(url)
                .thumbnail(THUMBNAIL_SIZE)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .dontTransform()
                .into(itemView.thumbnail)

            itemView.userAvatar.loadAvatar(item.userId)
            itemView.userUsername.text = context.getString(R.string.nickname, item.nickname)
            itemView.userName.text =
                context.getString(R.string.full_name, item.firstName, item.lastName)
            itemView.vlogPostDate.text = item.startDate

            itemView.userAvatar.setOnClickListener { profileClick.invoke(item) }
            itemView.userUsername.text = context.getString(R.string.nickname, item.nickname)
            itemView.userName.text = context.getString(R.string.full_name, item.firstName, item.lastName)
            itemView.vlogPostDate.text = item.startDate

            itemView.setOnClickListener { itemClick.invoke(item) }
        }
    }

    companion object {
        private val THUMBNAIL_SIZE = 0.1f
    }
}

private class VlogDiffCallback : DiffUtil.ItemCallback<ProfileVlogItem>() {
    override fun areItemsTheSame(
        oldItem: ProfileVlogItem,
        newItem: ProfileVlogItem
    ): Boolean =
        oldItem.vlogId == newItem.vlogId

    override fun areContentsTheSame(
        oldItem: ProfileVlogItem,
        newItem: ProfileVlogItem
    ): Boolean =
        oldItem == newItem
}
