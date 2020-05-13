package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.Reaction
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.UUID

data class ReactionEntity(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "vlogId") val vlogId: String,
    @field:Json(name = "datePosted") val datePosted: String
)

fun ReactionEntity.mapToDomain(): Reaction = Reaction(
    UUID.fromString(id),
    UUID.fromString(userId),
    UUID.fromString(vlogId),
    ZonedDateTime.parse(datePosted)
)

fun Reaction.mapToData(): ReactionEntity = ReactionEntity(
    id.toString(),
    userId.toString(),
    vlogId.toString(),
    datePosted.toInstant().toString()
)

fun List<ReactionEntity>.mapToDomain(): List<Reaction> = map { it.mapToDomain() }
fun List<Reaction>.mapToData(): List<ReactionEntity> = map { it.mapToData() }
