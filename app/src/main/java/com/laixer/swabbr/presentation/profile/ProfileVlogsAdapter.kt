package com.laixer.swabbr.presentation.profile

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.laixer.presentation.inflate
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.UserVlogItem
import kotlinx.android.synthetic.main.item_list_uservlog.view.*
import kotlinx.android.synthetic.main.item_list_uservlog.view.like_count
import kotlinx.android.synthetic.main.item_list_uservlog.view.thumbnail
import kotlinx.android.synthetic.main.item_list_uservlog.view.view_count
import kotlinx.android.synthetic.main.item_list_uservlog.view.vlogDuration
import kotlinx.android.synthetic.main.item_list_uservlog.view.vlogPostDate
import kotlinx.android.synthetic.main.item_list_vlog.view.*
import kotlinx.android.synthetic.main.item_list_vlog.view.processing_cover
import kotlinx.android.synthetic.main.item_list_vlog.view.reaction_count
import java.time.ZonedDateTime

class ProfileVlogsAdapter(
    private val vm: ProfileViewModel,
    private val token: String,
    private val onClick: (UserVlogItem) -> Unit,
    private val onDelete: ((UserVlogItem) -> Unit)? = null
) : ListAdapter<UserVlogItem, ProfileVlogsAdapter.ViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_uservlog)) {

        fun bind(item: UserVlogItem) = with(itemView) {
            if (item.vlog.data.dateStarted.isBefore(ZonedDateTime.now().minusMinutes(3))) {
                processing_cover.visibility = View.GONE


                /* We convert back to String because Glide's load(URL) function is deprecated because
                 of possible performance issues. */
                val url = item.vlog.thumbnailUri.toString() ?: ""
                val glideUrl = GlideUrl(
                    url,
                    LazyHeaders.Builder().addHeader("Authorization", "Bearer $token")
                        .build()
                )


                Glide.with(context)
                    .load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.1f)
                    .into(thumbnail)

                vlogPostDate.text =
                    context.getString(
                        R.string.date,
                        item.vlog.data.dateStarted.dayOfMonth,
                        item.vlog.data.dateStarted.monthValue,
                        item.vlog.data.dateStarted.year
                    )
                vlogDuration.text =
                    context.getString(R.string.duration, (Math.random() * 10).toInt(), (Math.random() * 60).toInt())
                reaction_count.text = context.getString(R.string.reaction_count, vm.getReactionCount(item.vlog.data.id))

                view_count.text = context.getString(R.string.view_count, item.vlog.data.views)
                like_count.text = context.getString(R.string.like_count, item.vlog.vlogLikeSummary.totalLikes)

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

private class ProfileDiffCallback : DiffUtil.ItemCallback<UserVlogItem>() {

    override fun areItemsTheSame(oldItem: UserVlogItem, newItem: UserVlogItem): Boolean =
        oldItem.vlog.data.id == newItem.vlog.data.id

    override fun areContentsTheSame(oldItem: UserVlogItem, newItem: UserVlogItem): Boolean =
        oldItem.vlog.data.id == newItem.vlog.data.id
}
