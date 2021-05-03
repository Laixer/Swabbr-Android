package com.laixer.swabbr.domain.model

import java.util.*

/**
 * Object representing a like summary for a vlog.
 */
data class VlogLikeSummary(
    val vlogId: UUID,
    val totalLikes: Int,
    val users: List<User>
) {
    companion object {
        /**
         *  Empty vlog like summary object.
         */
        fun emptyObject() = VlogLikeSummary(
            vlogId = UUID.randomUUID(),
            totalLikes = 0,
            users = emptyList()
        )
    }
}
