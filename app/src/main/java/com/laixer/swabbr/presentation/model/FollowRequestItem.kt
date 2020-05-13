package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.FollowStatus
import java.time.ZonedDateTime
import java.util.UUID

data class FollowRequestItem(
    val requesterId: UUID,
    val receiverId: UUID,
    val status: FollowStatus,
    val timeCreated: ZonedDateTime
)

fun FollowRequest.mapToPresentation(): FollowRequestItem =
    FollowRequestItem(
        this.requesterId,
        this.receiverId,
        this.status,
        this.timeCreated
    )
