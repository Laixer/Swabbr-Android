package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.Vlog
import java.io.Serializable
import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID

data class UserVlogItem(
    val userId: UUID,
    val nickname: String,
    val firstName: String,
    val lastName: String,
    val profileImageUrl: URL,
    val vlogId: UUID,
    val dateStarted: ZonedDateTime,
    val url: URL
) : Serializable

fun Pair<User, Vlog>.mapToPresentation(): UserVlogItem = UserVlogItem(
    this.first.id,
    this.first.nickname,
    this.first.firstName,
    this.first.lastName,
    this.first.profileImageUrl,
    this.second.id,
    this.second.dateStarted,
    this.second.url
)

fun List<Pair<User, Vlog>>.mapToPresentation(): List<UserVlogItem> = map { it.mapToPresentation() }
