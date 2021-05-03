package com.laixer.swabbr.data.model

import com.laixer.swabbr.domain.model.UserWithRelation
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.squareup.moshi.Json
import java.util.*

/**
 *  Model representing a user including its relation to some other user.
 *
 *  Note that the follow request status property is nullable. The
 *  [FollowRequestStatus.NONEXISTENT] enum flag only exists in this
 *  app, not in the API. Having a status as null indicates no follow
 *  request exists, hence we explicitly map this when going from data
 *  to domain.
 */
data class UserWithRelationEntity(
    @field:Json(name = "requestingUserId") val requestingUserId: UUID,
    @field:Json(name = "followRequestStatus") val followRequestStatus: Int?,
    @field:Json(name = "user") val user: UserEntity
)

/**
 *  Map a [VlogLikingUserWrapperEntity] from data to domain.
 */
fun UserWithRelationEntity.mapToDomain(): UserWithRelation = UserWithRelation(
    requestingUserId = requestingUserId,
    followRequestStatus = if (followRequestStatus == null) FollowRequestStatus.NONEXISTENT else FollowRequestStatus.values()[followRequestStatus],
    user = user.mapToDomain()
)

/**
 *  Map a collection of [VlogLikingUserWrapperEntity] from data to domain.
 */
fun List<UserWithRelationEntity>.mapToDomain(): List<UserWithRelation> = map { it.mapToDomain() }

