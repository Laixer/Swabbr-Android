package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.VlogWrapper

/**
 *  Item containing a vlog, its user and its vlog like summary.
 */
data class VlogWrapperItem(
    val vlog: VlogItem,
    val user: UserItem,
    val vlogLikeCount: Int,
    val reactionCount: Int
) {
    // TODO Fix this
    fun equals(compare: VlogWrapperItem): Boolean =
        this.vlog.id.equals(compare.vlog.id)
}

/**
 *  Maps a vlog wrapper from domain to presentation.
 */
fun VlogWrapper.mapToPresentation(): VlogWrapperItem = VlogWrapperItem(
    vlog.mapToPresentation(),
    user.mapToPresentation(),
    vlogLikeCount,
    reactionCount
)

/**
 *  Map a collection of vlog wrappers from domain to presentation.
 */
fun List<VlogWrapper>.mapToPresentation(): List<VlogWrapperItem> = map { it.mapToPresentation() }
