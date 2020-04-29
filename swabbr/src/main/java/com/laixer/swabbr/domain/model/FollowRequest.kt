package com.laixer.swabbr.domain.model

import java.time.ZonedDateTime
import java.util.UUID

data class FollowRequest(
    val requesterId: UUID,
    val receiverId: UUID,
    val status: FollowStatus,
    val timeCreated: ZonedDateTime
)

enum class FollowStatus(val value: String) {
    PENDING("pending"),
    FOLLOWING("accepted"),
    NOT_FOLLOWING("does_not_exist"),
    DECLINED("declined")
}
