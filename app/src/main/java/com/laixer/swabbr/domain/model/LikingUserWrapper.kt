package com.laixer.swabbr.domain.model

import com.laixer.swabbr.domain.types.FollowRequestStatus
import java.util.*

/**
 * Model representing a wrapper around a user that liked a vlog.
 */
data class LikingUserWrapper(
    val vlogOwnerId: UUID,
    val followRequestStatus: FollowRequestStatus,
    val vlogLike: VlogLike,
    val vlogLikingUser: User
)
