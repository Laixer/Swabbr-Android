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
    val totalLikesReceived: Int,
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
    id = id,
    firstName = firstName,
    lastName = lastName,
    gender = gender,
    country = country,
    nickname = nickname,
    profileImage = profileImage,
    totalLikesReceived = totalLikesReceived,
    totalFollowers = totalFollowers,
    totalFollowing = totalFollowing,
    totalReactionsGiven = totalReactionsGiven,
    totalReactionsReceived = totalReactionsReceived,
    totalVlogs = totalVlogs,
    totalViews = totalViews
)
