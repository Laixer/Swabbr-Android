package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.domain.model.Like
import com.laixer.swabbr.domain.model.LikeList
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.UUID

data class LikeEntity(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "vlogId") val vlogId: String,
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "timeCreated") val timeCreated: String
)

data class LikeListEntity(
    @field:Json(name = "totalLikeCount") val totalLikeCount: Int,
    @field:Json(name = "usersSimplified") val usersSimplified: List<SimplifiedUserEntity>
)

fun LikeListEntity.mapToDomain(): LikeList = LikeList(
    this.totalLikeCount,
    this.usersSimplified.mapToDomain()
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
