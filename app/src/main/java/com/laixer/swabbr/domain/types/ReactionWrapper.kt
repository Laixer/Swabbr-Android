package com.laixer.swabbr.domain.types

import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.User

/**
 *  Wrapper around a reaction and its user.
 */
data class ReactionWrapper(
    val reaction: Reaction,
    val user: User
)
