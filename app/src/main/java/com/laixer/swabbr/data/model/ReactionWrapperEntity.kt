package com.laixer.swabbr.data.model

import com.laixer.swabbr.domain.model.ReactionWrapper

/**
 * Entity representing a reaction wrapper.
 */
data class ReactionWrapperEntity(
    val reaction: ReactionEntity,
    val user: UserEntity
)

/**
 * Map a reaction wrapper from data to domain.
 */
fun ReactionWrapperEntity.mapToDomain(): ReactionWrapper = ReactionWrapper(
    reaction.mapToDomain(),
    user.mapToDomain()
)

/**
 * Map a collection of reactions from data to domain.
 */
fun List<ReactionWrapperEntity>.mapToDomain(): List<ReactionWrapper> = map { it.mapToDomain() }
