package com.laixer.swabbr.domain.types

/**
 * Enum representing the status of a follow request.
 */
enum class FollowRequestStatus(val value: Int) {
    PENDING(0),
    ACCEPTED(1),
    DECLINED(2),
    NONEXISTENT(3)
}
