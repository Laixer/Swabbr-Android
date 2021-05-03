package com.laixer.swabbr.presentation.utils.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.laixer.swabbr.presentation.model.VlogWrapperItem

/**
 *  Determines difference between [VlogWrapperItem]s.
 */
internal class VlogWrapperDiffCallback : DiffUtil.ItemCallback<VlogWrapperItem>() {

    override fun areItemsTheSame(oldItem: VlogWrapperItem, newItem: VlogWrapperItem): Boolean =
        oldItem.vlog.id == newItem.vlog.id

    override fun areContentsTheSame(oldItem: VlogWrapperItem, newItem: VlogWrapperItem): Boolean =
        oldItem.vlog.equals(newItem.vlog)
}
