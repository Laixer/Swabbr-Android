package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.UploadReaction
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.UUID

data class ReactionEntity(
    @field:Json(name = "reaction") val reaction: ReactionDataEntity,
    @field:Json(name = "thumbnailUri") val thumbnailUri: String
)

data class ReactionDataEntity(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "targetVlogId") val vlogId: String,
    @field:Json(name = "createDate") val datePosted: String,
    @field:Json(name = "isPrivate") val isPrivate: Boolean
)

data class UploadReactionEntity(
    @field:Json(name = "reaction") val reaction: ReactionDataEntity,
    @field:Json(name = "uploadUrl") val uploadUrl: String
)

data class ReactionListResponse(
    @field:Json(name = "reactionsTotalCount") val totalCount: Int,
    @field:Json(name = "reactionCount") val count: Int,
    @field:Json(name = "reactions") val reactions: List<ReactionEntity>
)

data class ReactionCount(
    @field:Json(name = "reactionCount") val count: Int
)

data class NewReaction(
    @field:Json(name = "targetVlogId") val targetVlogId: String,
    @field:Json(name = "isPrivate") val isPrivate: Boolean
)

fun ReactionDataEntity.mapToDomain(): Reaction = Reaction(
    UUID.fromString(id),
    UUID.fromString(userId),
    UUID.fromString(vlogId),
    ZonedDateTime.parse(datePosted),
    isPrivate
)

fun UploadReactionEntity.mapToDomain(): UploadReaction = UploadReaction(
    reaction.mapToDomain(),
    uploadUrl
)

fun Reaction.mapToData(): ReactionDataEntity = ReactionDataEntity(
    id.toString(),
    userId.toString(),
    targetVlogId.toString(),
    createDate.toInstant().toString(),
    isPrivate
)

fun List<ReactionEntity>.mapToDomain(): List<Reaction> = map { it.reaction.mapToDomain() }
fun List<Reaction>.mapToData(): List<ReactionDataEntity> = map { it.mapToData() }
