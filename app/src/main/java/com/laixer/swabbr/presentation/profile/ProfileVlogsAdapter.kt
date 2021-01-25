package com.laixer.swabbr.presentation.profile

import android.content.Context
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.auth.AuthUserViewModel
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import kotlinx.android.synthetic.main.include_user_stats.view.*
import kotlinx.android.synthetic.main.include_vlog_stats.view.*
import kotlinx.android.synthetic.main.item_list_uservlog.view.*
import kotlinx.android.synthetic.main.item_list_uservlog.view.thumbnail
import kotlinx.android.synthetic.main.item_list_vlog.view.like_count
import kotlinx.android.synthetic.main.item_list_vlog.view.processing_cover
import kotlinx.android.synthetic.main.item_list_vlog.view.reaction_count
import kotlinx.android.synthetic.main.item_list_vlog.view.view_count
import java.time.ZonedDateTime

class ProfileVlogsAdapter(
    private val context: Context,
    private val vm: ProfileViewModel,
    private val authUserVm: AuthUserViewModel,
    private val onClick: (VlogWrapperItem) -> Unit,
    private val onDelete: ((VlogWrapperItem) -> Unit)? = null
) : ListAdapter<VlogWrapperItem, ProfileVlogsAdapter.ViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_uservlog)) {

        fun bind(item: VlogWrapperItem) = with(itemView) {
            if (item.vlog.dateCreated.isBefore(ZonedDateTime.now().minusMinutes(3))) {
                processing_cover.visibility = View.GONE

                /* We convert back to String because Glide's load(URL) function is deprecated because
                 of possible performance issues. */
                item.vlog.thumbnailUri?.toString()?.let {
                    val glideUrl: GlideUrl? = GlideUrl(
                        it,
                        LazyHeaders.Builder().addHeader("Authorization", "Bearer ${authUserVm.getAuthToken()}")
                            .build()
                    )

                    glideUrl?.let {
                        Glide.with(context)
                            .load(glideUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .thumbnail(1F)
                            .placeholder(R.drawable.thumbnail_placeholder)
                            .into(thumbnail)
                    }
                }

                val timeLabels = DateUtils.getRelativeDateTimeString(context,
                    item.vlog.dateCreated.toInstant().toEpochMilli(),
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS,
                    0)
                    .split(',')

                post_date.text = timeLabels[0]
                post_date_label.text = timeLabels[1]

                reaction_count.text = context.getString(R.string.count, vm
                    .getReactionCount(item.vlog.id)
                    .blockingGet()) // TODO Blocking get
                view_count.text = context.getString(R.string.count, item.vlog.views)
                like_count.text = context.getString(R.string.count, item.vlogLikeSummary.totalLikes)

                setOnClickListener {
                    this.isEnabled = false
                    delete_button.isEnabled = false
                    onClick.invoke(item)
                }
                onDelete?.let {
                    delete_button.visibility = View.VISIBLE
                    delete_button.setOnClickListener {
                        this.isEnabled = false
                        delete_button.isEnabled = false
                        it(item)
                    }
                }

            }
        }
    }
}

private class ProfileDiffCallback : DiffUtil.ItemCallback<VlogWrapperItem>() {

    override fun areItemsTheSame(oldItem: VlogWrapperItem, newItem: VlogWrapperItem): Boolean =
        oldItem.vlog.id == newItem.vlog.id

    override fun areContentsTheSame(oldItem: VlogWrapperItem, newItem: VlogWrapperItem): Boolean =
        oldItem.vlog.equals(newItem.vlog)
}
