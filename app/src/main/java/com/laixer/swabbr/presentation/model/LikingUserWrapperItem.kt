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
data class LikingUserWrapperItem(
    val vlogOwnerId: UUID,
    var followRequestStatus: FollowRequestStatus,
    val vlogLikeEntity: VlogLikeItem,
    val vlogLikingUser: UserItem
)

/**
 *  Map a [LikingUserWrapper] from domain to presentation.
 */
fun LikingUserWrapper.mapToDomain(): LikingUserWrapperItem = LikingUserWrapperItem(
    vlogOwnerId = vlogOwnerId,
    followRequestStatus = followRequestStatus,
    vlogLikeEntity = vlogLikeEntity.mapToPresentation(),
    vlogLikingUser = vlogLikingUser.mapToPresentation()
)

/**
 *  Map a collection of [LikingUserWrapper] from domain to presentation
 */
fun List<LikingUserWrapper>.mapToPresentation(): List<LikingUserWrapperItem> = map { it.mapToDomain() }
