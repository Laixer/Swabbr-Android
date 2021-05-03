package com.laixer.swabbr.domain.types

/**
 * Enum representing a users follow mode.
 */
enum class FollowMode(val value: Int) {
    MANUAL(0),
    ACCEPT_ALL(1),
    DECLINE_ALL(2)
}
