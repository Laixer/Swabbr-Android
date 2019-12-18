package com.laixer.swabbr.domain.model

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val country: String,
    val email: String,
    val timezone: String,
    val totalVlogs: Int,
    val totalFollowers: Int,
    val totalFollowing: Int,
    val nickname: String,
    val profileImageUrl: String,
    val birthdate: String,
    val longitude: Double,
    val latitude: Double
)
