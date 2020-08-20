package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.domain.model.Like
import com.laixer.swabbr.domain.model.LikeList
import com.laixer.swabbr.domain.model.MinifiedUser
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
    @field:Json(name = "usersMinified") val usersMinified: List<MinifiedUserEntity>
)

data class MinifiedUserEntity(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "nickName") val nickname: String
)

fun LikeListEntity.mapToDomain(): LikeList = LikeList(
    this.totalLikeCount,
    this.usersMinified.mapToDomain()
)

fun MinifiedUserEntity.mapToDomain(): MinifiedUser = MinifiedUser(
    UUID.fromString(this.id),
    this.nickname
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

// Have to use Collection instead of List because Java sucks and erases types which causes same type signatures for List<T> functions.
fun Collection<MinifiedUserEntity>.mapToDomain(): List<MinifiedUser> = map { it.mapToDomain() }

