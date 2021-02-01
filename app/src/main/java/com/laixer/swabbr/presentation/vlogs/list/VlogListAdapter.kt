package com.laixer.swabbr.presentation.vlogs.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.auth.AuthUserViewModel
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.utils.loadAvatar
import kotlinx.android.synthetic.main.include_user_info.view.*
import kotlinx.android.synthetic.main.item_list_vlog.view.*

/**
 *  Re-usable adapter for displaying a list of [VlogWrapperItem]s.
 */
class VlogListAdapter constructor(
    private val vm: VlogListViewModel,
    private val itemClick: (VlogWrapperItem) -> Unit,
    private val profileClick: (VlogWrapperItem) -> Unit // TODO Is this correct?
) : ListAdapter<VlogWrapperItem, VlogListAdapter.ViewHolder>(VlogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    /**
     *  Inner class used to represent a single vlog.
     */
    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_vlog)) {
        /**
         *  Binds this [ViewHolder] to a specific [VlogWrapperItem] in the adapter.
         */
        fun bind(item: VlogWrapperItem) = with(itemView) {
            processing_cover.visibility = View.GONE

            // Load the thumbnail image.
            Glide.with(context)
                .load(GlideUrl(item.vlog.thumbnailUri.toString()))
                .placeholder(R.drawable.thumbnail_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.1f)
                .into(thumbnail)

            // Load information about the user.
            user_avatar.loadAvatar(item.user.profileImage, item.user.id)
            user_nickname.text = context.getString(R.string.nickname, item.user.nickname)
            item.user.firstName?.let {
                user_username.apply {
                    text = context.getString(R.string.full_name, it, item.user.lastName)
                    visibility = View.VISIBLE
                }
            }

            // Load information about the vlog and its metadata.
            vlogPostDate.text =
                context.getString(
                    R.string.date,
                    item.vlog.dateCreated.dayOfMonth,
                    item.vlog.dateCreated.monthValue,
                    item.vlog.dateCreated.year
                )

            vlogDuration.text = context.getString(
                R.string.duration,
                item.vlog.length.run { if (this != null) (this - this.rem(60)) / 60 else 0 },
                item.vlog.length?.rem(60) ?: 0
            )

            reaction_count.text = context.getString(
                R.string.reaction_count, vm
                    .getReactionCount(item.vlog.id)
                    .blockingGet()
            ) // TODO Blocking get

            view_count.text = context.getString(R.string.view_count, item.vlog.views)

            like_count.text = context.getString(R.string.like_count, item.vlogLikeSummary.totalLikes)

            user_avatar.setOnClickListener { profileClick.invoke(item) }
            user_nickname.text = context.getString(R.string.nickname, item.user.nickname)

            item.user.firstName?.let {
                itemView.user_username.apply {
                    text = context.getString(R.string.full_name, it, item.user.lastName)
                    visibility = View.VISIBLE
                }
            }

            setOnClickListener {
                itemClick.invoke(item)
            }
        }
    }

    companion object {
        private const val THUMBNAIL_SIZE = 1f
    }
}

private class VlogDiffCallback : DiffUtil.ItemCallback<VlogWrapperItem>() {

    override fun areItemsTheSame(oldItem: VlogWrapperItem, newItem: VlogWrapperItem): Boolean =
        oldItem.vlog.id == newItem.vlog.id

    override fun areContentsTheSame(oldItem: VlogWrapperItem, newItem: VlogWrapperItem): Boolean =
        oldItem.equals(newItem)
}
