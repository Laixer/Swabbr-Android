package com.laixer.swabbr.data.datasource.model

import android.net.Uri
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.types.VlogStatus
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.*

/**
 * Entity representing a single vlog.
 */
data class VlogEntity(
    @field:Json(name = "id") val id: UUID,
    @field:Json(name = "userId") val userId: UUID,
    @field:Json(name = "isPrivate") val isPrivate: Boolean,
    @field:Json(name = "dateCreated") val startDate: ZonedDateTime,
    @field:Json(name = "views") val views: Int,
    @field:Json(name = "length") val length: Int?,
    @field:Json(name = "vlogStatus") val vlogStatus: Int,
    @field:Json(name = "videoUri") val videoUri: Uri,
    @field:Json(name = "thumbnailUri") val thumbnail: Uri
)

/**
 * Map a vlog from data to domain.
 */
fun VlogEntity.mapToDomain(): Vlog = Vlog(
    id,
    userId,
    isPrivate,
    startDate,
    views,
    length,
    VlogStatus.values()[vlogStatus],
    videoUri,
    thumbnail
)

/**
 * Map a vlog from domain to data.
 */
fun Vlog.mapToData(): VlogEntity = VlogEntity(
    id,
    userId,
    isPrivate,
    dateStarted,
    views,
    length,
    vlogStatus.ordinal,
    videoUri,
    thumbnail
)

/**
 * Map a collection of vlogs from data to domain.
 */
fun List<VlogEntity>.mapToDomain(): List<Vlog> = map { it.mapToDomain() }
