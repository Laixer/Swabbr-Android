package com.laixer.swabbr.domain.model

import com.laixer.swabbr.domain.types.Gender
import java.util.*

/**
 * Object representing a single user including
 * statistics data for that user.
 */
data class UserWithStats(
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
