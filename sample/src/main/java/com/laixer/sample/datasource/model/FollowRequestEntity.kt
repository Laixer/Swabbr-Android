package com.laixer.sample.datasource.model

import com.laixer.sample.domain.model.FollowRequest
import com.laixer.sample.domain.model.User
import com.squareup.moshi.Json

data class FollowRequestEntity(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "requesterId") val requesterId: String,
    @field:Json(name = "receiverId") val receiverId: String,
    @field:Json(name = "status") val status: String,
    @field:Json(name = "timestamp") val timestamp: String
)

fun FollowRequestEntity.mapToDomain(): FollowRequest = FollowRequest(
    id,
    requesterId,
    receiverId,
    status,
    timestamp
)

fun List<FollowRequestEntity>.mapToDomain(): List<FollowRequest> = map { it.mapToDomain() }
