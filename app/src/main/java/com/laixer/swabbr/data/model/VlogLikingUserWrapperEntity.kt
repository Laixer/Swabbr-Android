package com.laixer.swabbr.data.model

import com.laixer.swabbr.domain.model.VlogLikingUserWrapper

/**
 * Entity representing a wrapper around a user that liked a vlog.
 */
class VlogLikingUserWrapperEntity()
// TODO

/**
 *  Map a [VlogLikingUserWrapperEntity] from data to domain.
 */
fun VlogLikingUserWrapperEntity.mapToDomain(): VlogLikingUserWrapper = VlogLikingUserWrapper()

/**
 *  Map a collection of [VlogLikingUserWrapperEntity] from data to domain.
 */
fun List<VlogLikingUserWrapperEntity>.mapToDomain(): List<VlogLikingUserWrapper> = map { it.mapToDomain() }

