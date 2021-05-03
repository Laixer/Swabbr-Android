package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.VlogLikeSummary
import java.util.*

/**
 *  Item representing a vlog like summary.
 */
data class VlogLikeSummaryItem(
    val vlogId: UUID,
    val totalLikes: Int,
    val users: List<UserItem>
)

/**
 *  Map a vlog like summary from domain to presentation.
 */
fun VlogLikeSummary.mapToPresentation(): VlogLikeSummaryItem = VlogLikeSummaryItem(
    vlogId,
    totalLikes,
    users.mapToPresentation()
)
