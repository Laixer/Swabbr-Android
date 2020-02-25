package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.FollowRequest

data class FollowRequestItem(
    val followRequestId: String,
    val requesterId: String,
    val receiverId: String,
    val status: Int,
    val timeCreated: String
)

fun FollowRequest.mapToPresentation(): FollowRequestItem =
    FollowRequestItem(
        this.followRequestId,
        this.requesterId,
        this.receiverId,
        this.status,
        this.timeCreated
    )
