package com.laixer.swabbr.domain.model

data class Settings(
    val private: Boolean,
    val dailyVlogRequestLimit: Int,
    val followMode: FollowMode
)

enum class FollowMode(val value: String) {
    MANUAL("manual"),
    ACCEPT_ALL("accept_all"),
    DECLINE_ALL("decline_all")
}
