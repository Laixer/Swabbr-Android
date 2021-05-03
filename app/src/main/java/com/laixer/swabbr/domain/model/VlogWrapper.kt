package com.laixer.swabbr.domain.model

/**
 * Object representing a vlog wrapper.
 */
data class VlogWrapper(
    val vlog: Vlog,
    val user: User,
    val vlogLikeCount: Int,
    val reactionCount: Int
)
