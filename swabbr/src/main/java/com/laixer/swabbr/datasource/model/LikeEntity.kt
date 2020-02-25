package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.Like
import com.squareup.moshi.Json

data class LikeEntity(
    @field:Json(name = "vlogLikeId") val vlogLikeId: String,
    @field:Json(name = "vlogId") val vlogId: String,
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "timeCreated") val timeCreated: String
)

fun LikeEntity.mapToDomain(): Like = Like(vlogLikeId, vlogId, userId, timeCreated)

fun List<LikeEntity>.mapToDomain(): List<Like> = map { it.mapToDomain() }
