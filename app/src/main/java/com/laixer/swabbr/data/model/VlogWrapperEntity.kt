package com.laixer.swabbr.data.model

import com.laixer.swabbr.domain.model.VlogWrapper

/**
 * Entity representing a vlog wrapper.
 */
data class VlogWrapperEntity(
    val vlog: VlogEntity,
    val user: UserEntity,
    val vlogLikeCount: Int,
    val reactionCount: Int
)

/**
 * Map a vlog wrapper from data to domain.
 */
fun VlogWrapperEntity.mapToDomain(): VlogWrapper = VlogWrapper(
    vlog.mapToDomain(),
    user.mapToDomain(),
    vlogLikeCount,
    reactionCount
)

/**
 * Map a vlog from domain to data.
 */
fun VlogWrapper.mapToData(): VlogWrapperEntity = VlogWrapperEntity(
    vlog.mapToData(),
    user.mapToData(),
    vlogLikeCount,
    reactionCount
)

/**
 * Map a collection of vlog wrappers from data to domain.
 */
fun List<VlogWrapperEntity>.mapToDomain(): List<VlogWrapper> = map { it.mapToDomain() }
