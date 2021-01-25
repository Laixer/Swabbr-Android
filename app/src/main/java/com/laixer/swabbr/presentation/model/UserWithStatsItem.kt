package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.UserWithStats
import com.laixer.swabbr.domain.types.Gender
import java.util.*

/**
 *  Item representing a user with statistics.
 */
data class UserWithStatsItem(
    val id: UUID,
    val firstName: String?,
    val lastName: String?,
    val gender: Gender,
    val country: String?,
    val nickname: String,
    val profileImage: String?,
    val totalLikes: Int,
    val totalFollowers: Int,
    val totalFollowing: Int,
    val totalReactionsGiven: Int,
    val totalReactionsReceived: Int,
    val totalVlogs: Int,
    val totalViews: Int
)

/**
 * Map a user with stats from domain to presentation.
 */
fun UserWithStats.mapToPresentation(): UserWithStatsItem = UserWithStatsItem(
    id,
    firstName,
    lastName,
    gender,
    country,
    nickname,
    profileImage,
    totalLikes,
    totalFollowers,
    totalFollowing,
    totalReactionsGiven,
    totalReactionsReceived,
    totalVlogs,
    totalViews
)
