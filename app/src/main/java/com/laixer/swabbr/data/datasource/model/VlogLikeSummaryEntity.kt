package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.domain.model.VlogLikeSummary
import com.squareup.moshi.Json
import java.util.*

/**
 * Entity representing a like summary for a vlog.
 */
data class VlogLikeSummaryEntity(
    @field:Json(name = "vlogId") val vlogId: UUID,
    @field:Json(name = "totalLikes") val totalLikes: Int,
    @field:Json(name = "users") val users: List<UserEntity>
)

/**
 * Map a vlog like summary from data to domain.
 */
fun VlogLikeSummaryEntity.mapToDomain(): VlogLikeSummary = VlogLikeSummary(
    vlogId,
    totalLikes,
    users.mapToDomain()
)
