package com.laixer.swabbr.presentation.vlogs.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.laixer.presentation.gone
import com.laixer.presentation.inflate
import com.laixer.presentation.visible
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.utils.diffcallback.VlogWrapperDiffCallback
import kotlinx.android.synthetic.main.item_list_vlog.view.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

/**
 *  Adapter for vlog display using card views.
 *
 *  @param selfId Id of the current user. TODO This is really really ugly. Centralize.
 *  @param onClickVlog Callback when we click a vlog item.
 *  @param onClickDelete Callback when we click the delete icon. Note that
 *                       this is only relevant if we own the vlog, hence
 *                       this being nullable.
 */
class VlogListCardAdapter(
    private val selfId: UUID,
    private val onClickVlog: (VlogWrapperItem) -> Unit,
    private val onClickDelete: ((VlogWrapperItem) -> Unit)? = null
) : ListAdapter<VlogWrapperItem, VlogListCardAdapter.ViewHolder>(VlogWrapperDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_vlog)) {
        /**
         *  Actual binding functionality for a single [VlogWrapperItem] in the list.
         */
        fun bind(item: VlogWrapperItem): Unit = with(itemView) {
            // Load the thumbnail image.
            Glide.with(context)
                .load(GlideUrl(item.vlog.thumbnailUri.toString()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(1F)
                .placeholder(R.drawable.thumbnail_placeholder)
                .into(image_view_vlog_thumbnail)

            text_view_vlog_date_created.text =
                item.vlog.dateCreated.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))

            // Click listeners
            include_user_info.setOnClickListener { onClickVlog.invoke(item) }

            // Conditional delete button binding
            if (selfId == item.vlog.userId) {
                button_delete_vlog.visible()
                button_delete_vlog.isEnabled = true

                if (onClickDelete == null) {
                    throw IllegalArgumentException("Specifiy onClickDelete when we own the vlog.")
                }
                button_delete_vlog.setOnClickListener { onClickDelete.invoke(item) }
            } else {
                button_delete_vlog.gone()
                button_delete_vlog.isEnabled = false
            }
        }
    }
}
