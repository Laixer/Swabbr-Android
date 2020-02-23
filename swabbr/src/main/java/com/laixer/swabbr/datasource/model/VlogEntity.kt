package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.Vlog
import com.squareup.moshi.Json

data class VlogEntity(
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "id") val id: String,
    @field:Json(name = "duration") val duration: String,
    @field:Json(name = "startDate") val startDate: String,
    @field:Json(name = "totalLikes") val totalLikes: Int,
    @field:Json(name = "totalReactions") val totalReactions: Int,
    @field:Json(name = "totalViews") val totalViews: Int,
    @field:Json(name = "isLive") val isLive: Boolean,
    @field:Json(name = "private") val isPrivate: Boolean
)

fun VlogEntity.mapToDomain(): Vlog = Vlog(
    userId,
    id,
    duration,
    startDate,
    totalLikes,
    totalReactions,
    totalViews,
    isLive,
    isPrivate
)

fun List<VlogEntity>.mapToDomain(): List<Vlog> = map { it.mapToDomain() }
