package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.types.ReactionWrapper

/**
 *  Item representing a single reaction.
 */
data class ReactionWrapperItem(
    val reaction: ReactionItem,
    val user: UserItem
)

// TODO These can be null.
/**
 * Map a reaction from domain to presentation.
 */
fun ReactionWrapper.mapToPresentation(): ReactionWrapperItem = ReactionWrapperItem(
    reaction.mapToPresentation(),
    user.mapToPresentation()
)

/**
 *  Map a collection of reaction wrappers from domain to presentation.
 */
fun List<ReactionWrapper>.mapToPresentation(): List<ReactionWrapperItem> = map { it.mapToPresentation() }
