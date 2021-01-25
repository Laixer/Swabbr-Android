package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.*

data class FollowRequestEntity(
    @field:Json(name = "requesterId") val requesterId: UUID,
    @field:Json(name = "receiverId") val receiverId: UUID,
    @field:Json(name = "followRequestStatus") val followRequestStatus: Int,
    @field:Json(name = "dateCreated") val dateCreated: ZonedDateTime,
    @field:Json(name = "dateUpdated") val dateUpdated: ZonedDateTime?
)

/**
 * Map a follow request from data to domain.
 */
fun FollowRequestEntity.mapToDomain(): FollowRequest = FollowRequest(
    requesterId,
    receiverId,
    FollowRequestStatus.values()[followRequestStatus],
    dateCreated,
    dateUpdated
)

/**
 * Map a collection of follow requests from data to domain.
 */
fun List<FollowRequestEntity>.mapToDomain(): List<FollowRequest> = map { it.mapToDomain() }

