package com.laixer.swabbr.domain.model

import java.time.ZonedDateTime
import java.util.*

/**
 * Represents a like for a vlog.
 */
data class VlogLike(
    val vlogId: UUID,
    val userId: UUID,
    val dateCreated: ZonedDateTime
)
