package com.laixer.swabbr.presentation.model

import android.net.Uri
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.types.ReactionStatus
import java.time.ZonedDateTime
import java.util.*

/**
 *  Item representing a single reaction.
 *  Note: length is in seconds.
 */
data class ReactionItem(
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

/**
 * Map a reaction from presentation to domain.
 */
fun ReactionItem.mapToDomain(): Reaction = Reaction(
    id,
    userId,
    targetVlogId,
    dateCreated,
    isPrivate,
    length,
    reactionStatus,
    videoUri,
    thumbnailUri
)

/**
 * Map a reaction from domain to presentation.
 */
fun Reaction.mapToPresentation(): ReactionItem = ReactionItem(
    id,
    userId,
    targetVlogId,
    dateCreated,
    isPrivate,
    length,
    reactionStatus,
    videoUri,
    thumbnailUri
)

/**
 * Map a collection of reaction from domain to data.
 */
fun List<Reaction>.mapToPresentation(): List<ReactionItem> = map { it.mapToPresentation() }
