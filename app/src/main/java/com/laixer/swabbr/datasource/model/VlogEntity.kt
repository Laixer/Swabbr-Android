package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.Vlog
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.UUID


data class VlogListResponse(
    @field:Json(name = "vlogsCount") val count: Int,
    @field:Json(name = "vlogs") val vlogs: List<VlogEntity>
)

data class VlogResponse(
    @field:Json(name = "vlog") val vlog: VlogEntity,
    @field:Json(name = "vlogLikes") val vlogLikes: List<String>
)

data class VlogEntity(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "isPrivate") val isPrivate: Boolean,
    @field:Json(name = "dateStarted") val startDate: String,
    @field:Json(name = "views") val views: Int
)

fun VlogEntity.mapToDomain(): Vlog = Vlog(
    UUID.fromString(id),
    UUID.fromString(userId),
    isPrivate,
    ZonedDateTime.parse(startDate),
    views
)

fun Vlog.mapToData(): VlogEntity = VlogEntity(
    id.toString(),
    userId.toString(),
    isPrivate,
    dateStarted.toInstant().toString(),
    views
)

fun List<VlogEntity>.mapToDomain(): List<Vlog> = map { it.mapToDomain() }
fun List<Vlog>.mapToData(): List<VlogEntity> = map { it.mapToData() }
