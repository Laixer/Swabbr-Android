package com.laixer.sample.domain.model

data class FollowRequest(
    val id: String,
    val requesterId: String,
    val receiverId: String,
    val status: String, //FollowRequestStatus,
    val timestamp: String
)
//
//enum class FollowRequestStatus {
//    PENDING,
//    ACCEPTED,
//    DECLINED
//}