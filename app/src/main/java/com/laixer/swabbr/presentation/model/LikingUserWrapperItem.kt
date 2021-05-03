package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.LikingUserWrapper
import com.laixer.swabbr.domain.types.FollowRequestStatus
import java.util.*

/**
 *  Model representing a wrapper around a user that liked a vlog.
 *  TODO [followRequestStatus] has been made modifiable so we can
 *       implement the quick fix for the design flaw issue at
 *       https://github.com/Laixer/Swabbr-Android/issues/141
 */
class LikingUserWrapperItem(
    requestingUserId: UUID,
    followRequestStatus: FollowRequestStatus,
    val vlogLikeItem: VlogLikeItem,
    user: UserItem
) : UserWithRelationItem(
    requestingUserId = requestingUserId,
    followRequestStatus = followRequestStatus,
    user = user
)

/**
 *  Map a [LikingUserWrapper] from domain to presentation.
 */
fun LikingUserWrapper.mapToDomain(): LikingUserWrapperItem = LikingUserWrapperItem(
    requestingUserId = vlogOwnerId,
    followRequestStatus = followRequestStatus,
    vlogLikeItem = vlogLike.mapToPresentation(),
    user = vlogLikingUser.mapToPresentation()
)

/**
 *  Map a collection of [LikingUserWrapper] from domain to presentation
 */
fun List<LikingUserWrapper>.mapToPresentation(): List<LikingUserWrapperItem> = map { it.mapToDomain() }
