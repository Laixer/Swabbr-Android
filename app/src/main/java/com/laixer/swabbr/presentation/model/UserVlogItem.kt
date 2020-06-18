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
    val firstName: String?,
    val lastName: String?,
    val profileImage: String?,
    val vlogId: UUID,
    val dateStarted: ZonedDateTime,
    val views: Int
) : Serializable {
    val url = with(idList.random()) {
        URL("https://assets.mixkit.co/videos/$this/$this-720.mp4")
    }
}

fun Pair<User, Vlog>.mapToPresentation(): UserVlogItem = UserVlogItem(
    this.first.id,
    this.first.nickname,
    this.first.firstName,
    this.first.lastName,
    this.first.profileImage,
    this.second.id,
    this.second.dateStarted,
    this.second.views
)

fun List<Pair<User, Vlog>>.mapToPresentation(): List<UserVlogItem> = map { it.mapToPresentation() }
