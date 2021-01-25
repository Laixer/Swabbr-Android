package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.types.VlogWrapper

/**
 *  Item containing a vlog, its user and its vlog like summary.
 */
data class VlogWrapperItem(
    val user: UserItem,
    val vlog: VlogItem,
    var vlogLikeSummary: VlogLikeSummaryItem
) {
    fun equals(compare: VlogWrapperItem): Boolean =
        this.vlog.id.equals(compare.vlog.id)
}

// TODO These can be null.
/**
 *  Maps a vlog wrapper from domain to presentation.
 */
fun VlogWrapper.mapToPresentation(): VlogWrapperItem = VlogWrapperItem(
    user.mapToPresentation(),
    vlog.mapToPresentation(),
    vlogLikeSummary.mapToPresentation()
)

/**
 *  Map a collection of vlog wrappers from domain to presentation.
 */
fun List<VlogWrapper>.mapToPresentation(): List<VlogWrapperItem> = map { it.mapToPresentation() }
