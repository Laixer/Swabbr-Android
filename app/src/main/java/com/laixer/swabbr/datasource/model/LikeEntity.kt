package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.Like
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.UUID

data class LikeEntity(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "vlogId") val vlogId: String,
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "timeCreated") val timeCreated: String
)

fun LikeEntity.mapToDomain(): Like = Like(
    UUID.fromString(id),
    UUID.fromString(vlogId),
    UUID.fromString(userId),
    ZonedDateTime.parse(timeCreated)
)

fun Like.mapToData(): LikeEntity = LikeEntity(
    id.toString(),
    vlogId.toString(),
    userId.toString(),
    timeCreated.toInstant().toString()
)

fun List<LikeEntity>.mapToDomain(): List<Like> = map { it.mapToDomain() }
fun List<Like>.mapToData(): List<LikeEntity> = map { it.mapToData() }
