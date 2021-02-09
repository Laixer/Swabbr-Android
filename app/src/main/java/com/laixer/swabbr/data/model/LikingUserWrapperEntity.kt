package com.laixer.swabbr.data.model

import com.laixer.swabbr.domain.model.LikingUserWrapper
import com.squareup.moshi.Json
import java.util.*

/**
 * Entity representing a wrapper around a user that liked a vlog.
 */
class VlogLikingUserWrapperEntity(
    @field:Json(name = "vlogOwnerId") val vlogOwnerId: UUID,
    @field:Json(name = "isVlogOwnerFollowingVlogLikingUser") val isVlogOwnerFollowingVlogLikingUser: Boolean,
    @field:Json(name = "vlogLike") val vlogLikeEntity: VlogLikeEntity,
    @field:Json(name = "vlogLikingUser") val vlogLikingUser: UserEntity
)

/**
 *  Map a [VlogLikingUserWrapperEntity] from data to domain.
 */
fun VlogLikingUserWrapperEntity.mapToDomain(): LikingUserWrapper = LikingUserWrapper(
    vlogOwnerId = vlogOwnerId,
    isVlogOwnerFollowingVlogLikingUser = isVlogOwnerFollowingVlogLikingUser,
    vlogLikeEntity = vlogLikeEntity.mapToDomain(),
    vlogLikingUser = vlogLikingUser.mapToDomain()
)

/**
 *  Map a collection of [VlogLikingUserWrapperEntity] from data to domain.
 */
fun List<VlogLikingUserWrapperEntity>.mapToDomain(): List<LikingUserWrapper> = map { it.mapToDomain() }

