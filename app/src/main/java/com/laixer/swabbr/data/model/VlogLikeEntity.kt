package com.laixer.swabbr.data.model

import com.laixer.swabbr.domain.model.VlogLike
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.UUID

/**
 * Entity representing a single vlog like.
 */
data class VlogLikeEntity(
    @field:Json(name = "vlogId") val vlogId: UUID,
    @field:Json(name = "userId") val userId: UUID,
    @field:Json(name = "dateCreated") val dateCreated: ZonedDateTime
)

/**
 * Map a vlog like from data to domain.
 */
fun VlogLikeEntity.mapToDomain(): VlogLike = VlogLike(
    vlogId,
    userId,
    dateCreated
)

/**
 * Map a collection of vlog likes from data to domain.
 */
fun List<VlogLikeEntity>.mapToDomain(): List<VlogLike> = map { it.mapToDomain() }
