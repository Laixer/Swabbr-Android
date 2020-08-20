package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.domain.model.UserStatistics
import com.squareup.moshi.Json

data class UserStatisticsEntity(
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "totalLikes") val totalLikes: Int,
    @field:Json(name = "totalFollowers") val totalFollowers: Int,
    @field:Json(name = "totalFollowing") val totalFollowing: Int,
    @field:Json(name = "totalReactionsGiven") val totalReactionsGiven: Int,
    @field:Json(name = "totalReactionsReceived") val totalReactionsReceived: Int,
    @field:Json(name = "totalVlogs") val totalVlogs: Int,
    @field:Json(name = "totalViews") val totalViews: Int
)

fun UserStatisticsEntity.mapToDomain(): UserStatistics = UserStatistics(
    userId,
    totalLikes,
    totalFollowers,
    totalFollowing,
    totalReactionsGiven,
    totalReactionsReceived,
    totalVlogs,
    totalViews
)
