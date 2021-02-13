package com.laixer.swabbr.domain.model

import com.laixer.swabbr.domain.types.FollowRequestStatus
import java.util.*

/**
 * Model representing a user including its relation to some other user.
 */
data class UserWithRelation(
    val requestingUserId: UUID,
    val followRequestStatus: FollowRequestStatus,
    val user: User
)
