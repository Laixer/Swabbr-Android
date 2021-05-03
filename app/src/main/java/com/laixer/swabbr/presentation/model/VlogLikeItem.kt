package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.VlogLike
import java.time.ZonedDateTime
import java.util.*

/**
 *  Item representing a single vlog like.
 */
data class VlogLikeItem(
    val vlogId: UUID,
    val userId: UUID, val
    dateCreated: ZonedDateTime
)

/**
 *  Map a vlog like from domain to presentation.
 */
fun VlogLike.mapToPresentation(): VlogLikeItem = VlogLikeItem(
    vlogId,
    userId,
    dateCreated
)
