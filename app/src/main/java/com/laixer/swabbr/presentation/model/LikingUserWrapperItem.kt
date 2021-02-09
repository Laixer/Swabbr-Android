package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.LikingUserWrapper
import java.util.*

/**
 * Model representing a wrapper around a user that liked a vlog.
 */
data class LikingUserWrapperItem(
    val vlogOwnerId: UUID,
    val isVlogOwnerFollowingVlogLikingUser: Boolean,
    val vlogLikeEntity: VlogLikeItem,
    val vlogLikingUser: UserItem
)

/**
 *  Map a [LikingUserWrapper] from domain to presentation.
 */
fun LikingUserWrapper.mapToDomain(): LikingUserWrapperItem = LikingUserWrapperItem(
    vlogOwnerId = vlogOwnerId,
    isVlogOwnerFollowingVlogLikingUser = isVlogOwnerFollowingVlogLikingUser,
    vlogLikeEntity = vlogLikeEntity.mapToPresentation(),
    vlogLikingUser = vlogLikingUser.mapToPresentation()
)

/**
 *  Map a collection of [LikingUserWrapper] from domain to presentation
 */
fun List<LikingUserWrapper>.mapToPresentation(): List<LikingUserWrapperItem> = map { it.mapToDomain() }
