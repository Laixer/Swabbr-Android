package com.laixer.swabbr.data.datasource.model

import android.net.Uri
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.types.ReactionStatus
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.*

/**
 * Entity representing a single reaction.
 * Note: length is in seconds.
 */
data class ReactionEntity(
    @field:Json(name = "id") val id: UUID,
    @field:Json(name = "userId") val userId: UUID,
    @field:Json(name = "targetVlogId") val targetVlogId: UUID,
    @field:Json(name = "dateCreated") val dateCreated: ZonedDateTime,
    @field:Json(name = "isPrivate") val isPrivate: Boolean,
    @field:Json(name = "length") val length: Int?,
    @field:Json(name = "reactionStatus") val reactionStatus: Int,
    @field:Json(name = "videoUri") val videoUri: Uri?,
    @field:Json(name = "thumbnailUri") val thumbnailUri: Uri?
)

/**
 * Map a reaction from data to domain.
 */
fun ReactionEntity.mapToDomain(): Reaction = Reaction(
    id,
    userId,
    targetVlogId,
    dateCreated,
    isPrivate,
    length,
    ReactionStatus.values()[reactionStatus],
    videoUri,
    thumbnailUri
)

/**
 * Map a reaction from domain to data.
 */
fun Reaction.mapToData(): ReactionEntity = ReactionEntity(
    id,
    userId,
    targetVlogId,
    dateCreated,
    isPrivate,
    length,
    reactionStatus.ordinal,
    videoUri,
    thumbnailUri
)

/**
 * Map a collection of reactions from data to domain.
 */
fun List<ReactionEntity>.mapToDomain(): List<Reaction> = map { it.mapToDomain() }
