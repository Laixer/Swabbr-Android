package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.FollowStatus
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.UUID

data class FollowRequestEntity(
    @field:Json(name = "requesterId") val requesterId: String,
    @field:Json(name = "receiverId") val receiverId: String,
    @field:Json(name = "status") val followStatus: String,
    @field:Json(name = "timeCreated") val timeCreated: String
)

fun FollowRequestEntity.mapToDomain(): FollowRequest = FollowRequest(
    UUID.fromString(requesterId),
    UUID.fromString(receiverId),
    FollowStatus.values().first { it.value == followStatus },
    ZonedDateTime.parse(timeCreated)
)

fun FollowRequest.mapToData(): FollowRequestEntity = FollowRequestEntity(
    requesterId.toString(),
    receiverId.toString(),
    status.value,
    timeCreated.toInstant().toString()
)

fun List<FollowRequestEntity>.mapToDomain(): List<FollowRequest> = map { it.mapToDomain() }
fun List<FollowRequest>.mapToData(): List<FollowRequestEntity> = map { it.mapToData() }
