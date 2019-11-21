package com.laixer.sample.presentation.model

import com.laixer.sample.domain.model.FollowRequest

data class FollowRequestItem(
    val id: String,
    val receiverId: String,
    val status: String,
    val timestamp: String
)

fun FollowRequest.mapToPresentation(): FollowRequestItem =
    FollowRequestItem(
        this.id,
        this.receiverId,
        this.status,
        this.timestamp
    )
