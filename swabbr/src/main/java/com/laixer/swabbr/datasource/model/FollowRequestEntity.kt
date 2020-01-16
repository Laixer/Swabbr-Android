package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.FollowRequest
import com.squareup.moshi.Json

data class FollowRequestEntity(
    @field:Json(name = "followRequestId") val followRequestId: String,
    @field:Json(name = "requesterId") val requesterId: String,
    @field:Json(name = "receiverId") val receiverId: String,
    @field:Json(name = "status") val status: Int,
    @field:Json(name = "timeCreated") val timeCreated: String
)

fun FollowRequestEntity.mapToDomain(): FollowRequest =
    FollowRequest(followRequestId, requesterId, receiverId, status, timeCreated)
