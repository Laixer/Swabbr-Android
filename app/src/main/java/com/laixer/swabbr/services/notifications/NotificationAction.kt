package com.laixer.swabbr.services.notifications

/**
 *  Represents the type of notification action.
 */
enum class NotificationAction(val value: Int) {
    FOLLOWED_PROFILE_VLOG_POSTED(0),
    VLOG_GAINED_LIKE(1),
    VLOG_NEW_REACTION(2),
    VLOG_RECORD_REQUEST(3)
}
