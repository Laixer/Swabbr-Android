package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Gender
import com.laixer.swabbr.domain.model.User
import java.net.URL
import java.time.LocalDate
import java.util.TimeZone
import java.util.UUID

data class UserItem(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val gender: Gender,
    val country: String,
    val email: String,
    val timezone: TimeZone,
    val totalVlogs: Int,
    val totalFollowers: Int,
    val totalFollowing: Int,
    val nickname: String,
    val profileImageUrl: URL,
    val birthdate: LocalDate
)

fun User.mapToPresentation(): UserItem = UserItem(
    this.id,
    this.firstName,
    this.lastName,
    this.gender,
    this.country,
    this.email,
    this.timezone,
    this.totalVlogs,
    this.totalFollowers,
    this.totalFollowing,
    this.nickname,
    this.profileImageUrl,
    this.birthdate
)

fun UserItem.mapToDomain(): User = User(
    this.id,
    this.firstName,
    this.lastName,
    this.gender,
    this.country,
    this.email,
    this.timezone,
    this.totalVlogs,
    this.totalFollowers,
    this.totalFollowing,
    this.nickname,
    this.profileImageUrl,
    this.birthdate
)

fun List<UserItem>.mapToDomain(): List<User> = map { it.mapToDomain() }
fun List<User>.mapToPresentation(): List<UserItem> = map { it.mapToPresentation() }
