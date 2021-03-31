package com.laixer.swabbr.services.users

/**
 *  Class indicating the state of our user service.
 */
enum class UserServiceState(val value: Int) {
    NO_USER(0),
    LOGGED_IN(1),
    LOGGED_IN_REFRESHING(2),
    ERROR(3)
}
