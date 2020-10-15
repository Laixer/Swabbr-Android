package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.domain.model.Reaction
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.UUID

data class ReactionEntity(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "targetVlogId") val vlogId: String,
    @field:Json(name = "createDate") val datePosted: String,
    @field:Json(name = "isPrivate") val isPrivate: Boolean
)

data class ReactionListResponse(
    @field:Json(name = "reactionCount") val count: Int,
    @field:Json(name = "reactions") val reactions: List<ReactionEntity>
)

data class ReactionCount(
    @field:Json(name = "reactionCount") val count: Int
)

fun ReactionEntity.mapToDomain(): Reaction = Reaction(
    UUID.fromString(id),
    UUID.fromString(userId),
    UUID.fromString(vlogId),
    ZonedDateTime.parse(datePosted),
    isPrivate
)

fun Reaction.mapToData(): ReactionEntity = ReactionEntity(
    id.toString(),
    userId.toString(),
    vlogId.toString(),
    datePosted.toInstant().toString(),
    isPrivate
)

fun List<ReactionEntity>.mapToDomain(): List<Reaction> = map { it.mapToDomain() }
fun List<Reaction>.mapToData(): List<ReactionEntity> = map { it.mapToData() }
