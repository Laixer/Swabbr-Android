package com.laixer.swabbr.domain.model

import java.time.LocalDate
import java.util.TimeZone
import java.util.UUID

data class User(
    val id: UUID,
    val firstName: String?,
    val lastName: String?,
    val gender: Gender,
    val country: String?,
    val email: String,
    val timezone: TimeZone,
    val totalVlogs: Int,
    val totalFollowers: Int,
    val totalFollowing: Int,
    val nickname: String,
    val profileImage: String?,
    val birthdate: LocalDate?
)

enum class Gender(val value: String) {
    FEMALE("female"),
    MALE("male"),
    UNSPECIFIED("unspecified")
}
