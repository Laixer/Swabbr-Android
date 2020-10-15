package com.laixer.swabbr.data.datasource.model

import android.net.Uri
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.VlogData
import com.laixer.swabbr.domain.model.VlogLikeSummary
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.UUID


data class VlogListResponseEntity(
    @field:Json(name = "vlogsCount") val count: Int,
    @field:Json(name = "vlogs") val vlogs: List<VlogResponseEntity>
)

data class VlogResponseEntity(
    @field:Json(name = "vlog") val vlog: VlogEntity,
    @field:Json(name = "vlogLikeSummary") val vlogLikeSummary: VlogLikeSummaryEntity,
    @field:Json(name = "thumbnailUri") val thumbnailUri: String
)

data class VlogEntity(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "isPrivate") val isPrivate: Boolean,
    @field:Json(name = "dateStarted") val startDate: String,
    @field:Json(name = "views") val views: Int
)

data class VlogLikeSummaryEntity(
    @field:Json(name = "vlogId") val vlogId: String,
    @field:Json(name = "totalLikes") val totalLikes: Int,
    @field:Json(name = "simplifiedUsers") val simplifiedUsers: List<SimplifiedUserEntity>
)



fun VlogEntity.mapToDomain(): VlogData = VlogData(
    UUID.fromString(id),
    UUID.fromString(userId),
    isPrivate,
    ZonedDateTime.parse(startDate),
    views
)

fun VlogResponseEntity.mapToDomain(): Vlog = Vlog(
    vlog.mapToDomain(),
    vlogLikeSummary.mapToDomain(),
    Uri.parse(thumbnailUri)
)

fun VlogData.mapToData(): VlogEntity = VlogEntity(
    id.toString(),
    userId.toString(),
    isPrivate,
    dateStarted.toInstant().toString(),
    views
)

fun VlogLikeSummaryEntity.mapToDomain(): VlogLikeSummary = VlogLikeSummary(
    UUID.fromString(vlogId),
    totalLikes,
    simplifiedUsers.map { it.mapToDomain() }
)

fun Vlog.mapToData(): VlogResponseEntity = VlogResponseEntity(
    data.mapToData(),
    vlogLikeSummary.mapToData(),
    thumbnailUri.toString()
)

fun VlogLikeSummary.mapToData(): VlogLikeSummaryEntity = VlogLikeSummaryEntity(
    vlogId.toString(),
    totalLikes,
    simplifiedUsers.map { it.mapToData() }
)

fun List<VlogEntity>.mapToDomain(): List<VlogData> = map { it.mapToDomain() }
fun List<Vlog>.mapToData(): List<VlogResponseEntity> = map { it.mapToData() }
