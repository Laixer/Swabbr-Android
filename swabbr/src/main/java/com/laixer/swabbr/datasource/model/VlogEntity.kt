package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.Vlog
import com.squareup.moshi.Json
import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID

val idList = listOf(
    1261, 1240, 1164, 1173, 2721, 1192, 1196, 1198, 1203, 1237, 1238, 1487, 1166,
    1170, 1178, 1181, 1183, 1184, 1185, 1186, 1187, 1188, 1191, 1194, 1197, 1165, 1200, 1168, 1468, 1548, 1541,
    1545, 1555, 1599, 1600, 1634, 1293, 1858, 1861, 1547, 2145, 2284, 2963, 2962, 2780, 3351, 3354, 2915, 3191,
    2923, 2285, 2308, 2286, 2306, 2307
)

data class VlogListResponse(
    @field:Json(name = "vlogsCount") val count: Int,
    @field:Json(name = "vlogs") val vlogs: List<VlogEntity>
)

data class VlogEntity(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "downloadUrl") val url: String?,
    @field:Json(name = "isPrivate") val isPrivate: Boolean,
    @field:Json(name = "dateStarted") val startDate: String
)

fun VlogEntity.mapToDomain(): Vlog = Vlog(
    UUID.fromString(id),
    UUID.fromString(userId),
    url?.let { URL(it) } ?: with(idList.random()) {
        URL("https://assets.mixkit.co/videos/3351/3351-720.mp4")
    },
    isPrivate,
    ZonedDateTime.parse(startDate)
)

fun Vlog.mapToData(): VlogEntity = VlogEntity(
    id.toString(),
    userId.toString(),
    url.toString(),
    isPrivate,
    dateStarted.toInstant().toString()
)

fun List<VlogEntity>.mapToDomain(): List<Vlog> = map { it.mapToDomain() }
fun List<Vlog>.mapToData(): List<VlogEntity> = map { it.mapToData() }
