package com.laixer.swabbr.domain.model

data class FollowRequest(
    val followRequestId: String,
    val requesterId: String,
    val receiverId: String,
    val status: Int,
    val timeCreated: String
)
