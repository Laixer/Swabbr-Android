package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.UserStatistics

data class UserStatisticsItem(
    val userId: String,
    val totalLikes: Int,
    val totalFollowers: Int,
    val totalFollowing: Int,
    val totalReactionsGiven: Int,
    val totalReactionsReceived: Int,
    val totalVlogs: Int,
    val totalViews: Int
)

fun UserStatistics.mapToPresentation(): UserStatisticsItem = UserStatisticsItem(
    this.userId,
    this.totalLikes,
    this.totalFollowers,
    this.totalFollowing,
    this.totalReactionsGiven,
    this.totalReactionsReceived,
    this.totalVlogs,
    this.totalViews
)
