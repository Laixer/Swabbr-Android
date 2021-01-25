package com.laixer.swabbr.domain.model

import android.net.Uri
import com.laixer.swabbr.domain.types.ReactionStatus
import java.time.ZonedDateTime
import java.util.*

/**
 * Object representing a reaction to a vlog.
 * Note: lenth is in seconds.
 */
data class Reaction(
    val id: UUID,
    val userId: UUID,
    val targetVlogId: UUID,
    val dateCreated: ZonedDateTime,
    val isPrivate: Boolean,
    val length: Int?,
    val reactionStatus: ReactionStatus,
    val videoUri: Uri?,
    val thumbnailUri: Uri?
)
