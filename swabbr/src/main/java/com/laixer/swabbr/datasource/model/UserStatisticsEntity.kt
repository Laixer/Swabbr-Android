package com.laixer.swabbr.datasource.model

import com.squareup.moshi.Json

data class UserStatisticsEntity(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "totalLikes") val totalLikes: Int,
    @field:Json(name = "totalFollowers") val totalFollowers: Int,
    @field:Json(name = "totalLikes") val totalFollowing: Int,
    @field:Json(name = "totalLikes") val totalReactionsGiven: Int,
    @field:Json(name = "totalLikes") val totalReactionsReceived: Int,
    @field:Json(name = "totalLikes") val totalVlogs: Int,
    @field:Json(name = "totalLikes") val totalViews: Int
)
