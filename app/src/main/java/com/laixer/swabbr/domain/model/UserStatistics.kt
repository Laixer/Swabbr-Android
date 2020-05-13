package com.laixer.swabbr.domain.model

data class UserStatistics(
    val userId: String,
    val totalLikes: Int,
    val totalFollowers: Int,
    val totalFollowing: Int,
    val totalReactionsGiven: Int,
    val totalReactionsReceived: Int,
    val totalVlogs: Int,
    val totalViews: Int
)
