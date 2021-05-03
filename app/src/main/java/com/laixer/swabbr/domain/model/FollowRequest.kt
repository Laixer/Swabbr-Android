package com.laixer.swabbr.domain.model

import com.laixer.swabbr.domain.types.FollowRequestStatus
import java.time.ZonedDateTime
import java.util.*

/**
 * Represents a single follow request between two users.
 */
data class FollowRequest(
    val requesterId: UUID,
    val receiverId: UUID,
    val followRequestStatus: FollowRequestStatus,
    val dateCreated: ZonedDateTime,
    val dateUpdated: ZonedDateTime?
)
