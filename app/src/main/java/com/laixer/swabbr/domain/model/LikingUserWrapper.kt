package com.laixer.swabbr.domain.model

import java.util.*

/**
 * Model representing a wrapper around a user that liked a vlog.
 */
data class LikingUserWrapper(
    val vlogOwnerId: UUID,
    val isVlogOwnerFollowingVlogLikingUser: Boolean,
    val vlogLikeEntity: VlogLike,
    val vlogLikingUser: User
)
