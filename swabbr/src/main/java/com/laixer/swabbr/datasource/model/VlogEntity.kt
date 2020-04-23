package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.Vlog
import com.squareup.moshi.Json
import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID

data class VlogListResponse(
    @field:Json(name = "vlogsCount") val count: Int,
    @field:Json(name = "vlogs") val vlogs: List<VlogEntity>
)

data class VlogEntity(
    @field:Json(name = "vlogId") val id: String,
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "downloadUrl") val url: String,
    @field:Json(name = "isPrivate") val isPrivate: Boolean,
    @field:Json(name = "isLive") val isLive: Boolean,
    @field:Json(name = "dateStarted") val startDate: String,
    @field:Json(name = "totalViews") val totalViews: Int = 0,
    @field:Json(name = "totalReactions") val totalReactions: Int = 0,
    @field:Json(name = "likes") val likes: List<LikeEntity>
)

fun VlogEntity.mapToDomain(): Vlog = Vlog(
    UUID.fromString(id),
    UUID.fromString(userId),
    isPrivate,
    isLive,
    ZonedDateTime.parse(startDate),
    totalViews,
    totalReactions,
    likes.mapToDomain(),
    URL(url)
)

fun Vlog.mapToData(): VlogEntity = VlogEntity(
    id.toString(),
    userId.toString(),
    url.toString(),
    isPrivate,
    isLive,
    dateStarted.toInstant().toString(),
    totalViews,
    totalReactions,
    likes.mapToData()
)

fun List<VlogEntity>.mapToDomain(): List<Vlog> = map { it.mapToDomain() }
fun List<Vlog>.mapToData(): List<VlogEntity> = map { it.mapToData() }
