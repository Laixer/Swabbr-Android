package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.Vlog
import com.squareup.moshi.Json

data class VlogEntity(
    @field:Json(name = "vlogId") val vlogId: String,
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "isPrivate") val isPrivate: Boolean,
    @field:Json(name = "isLive") val isLive: Boolean,
    @field:Json(name = "dateStarted") val startDate: String,
    @field:Json(name = "likes") val likes: List<LikeEntity>
)

fun VlogEntity.mapToDomain(): Vlog = Vlog(
    vlogId,
    userId,
    isPrivate,
    isLive,
    startDate,
    likes.mapToDomain()
)

fun List<VlogEntity>.mapToDomain(): List<Vlog> = map { it.mapToDomain() }
