package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.LikingUserWrapper
import com.laixer.swabbr.domain.model.UserWithRelation
import com.laixer.swabbr.domain.types.FollowRequestStatus
import java.util.*

// TODO Look at polymorphism for this.
/**
 *  Model representing a wrapper around a user that liked a vlog.
 *  TODO [followRequestStatus] has been made modifiable so we can
 *       implement the quick fix for the design flaw issue at
 *       https://github.com/Laixer/Swabbr-Android/issues/141
 */
open class UserWithRelationItem(
    val requestingUserId: UUID,
    var followRequestStatus: FollowRequestStatus,
    val user: UserItem
)

/**
 *  Map a [LikingUserWrapper] from domain to presentation.
 */
fun UserWithRelation.mapToDomain(): UserWithRelationItem = UserWithRelationItem(
    requestingUserId = requestingUserId,
    followRequestStatus = followRequestStatus,
    user = user.mapToPresentation()
)

/**
 *  Map a collection of [LikingUserWrapper] from domain to presentation
 */
fun List<UserWithRelation>.mapToPresentation(): List<UserWithRelationItem> = map { it.mapToDomain() }
