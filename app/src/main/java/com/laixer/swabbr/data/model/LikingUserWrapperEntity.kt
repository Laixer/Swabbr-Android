package com.laixer.swabbr.data.model

import com.laixer.swabbr.domain.model.LikingUserWrapper
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.squareup.moshi.Json
import java.util.*

/**
 *  Entity representing a wrapper around a user that liked a vlog.
 *
 *  Note that the follow request status property is nullable. The
 *  [FollowRequestStatus.NONEXISTENT] enum flag only exists in this
 *  app, not in the API. Having a status as null indicates no follow
 *  request exists, hence we explicitly map this when going from data
 *  to domain.
 */
data class VlogLikingUserWrapperEntity(
    @field:Json(name = "requestingUserId") val vlogOwnerId: UUID,
    @field:Json(name = "followRequestStatus") val followRequestStatus: Int?,
    @field:Json(name = "vlogLike") val vlogLikeEntity: VlogLikeEntity,
    @field:Json(name = "user") val vlogLikingUser: UserEntity
)

/**
 *  Map a [VlogLikingUserWrapperEntity] from data to domain.
 */
fun VlogLikingUserWrapperEntity.mapToDomain(): LikingUserWrapper = LikingUserWrapper(
    vlogOwnerId = vlogOwnerId,
    followRequestStatus = if (followRequestStatus == null) FollowRequestStatus.NONEXISTENT else FollowRequestStatus.values()[followRequestStatus],
    vlogLikeEntity = vlogLikeEntity.mapToDomain(),
    vlogLikingUser = vlogLikingUser.mapToDomain()
)

/**
 *  Map a collection of [VlogLikingUserWrapperEntity] from data to domain.
 */
fun List<VlogLikingUserWrapperEntity>.mapToDomain(): List<LikingUserWrapper> = map { it.mapToDomain() }

