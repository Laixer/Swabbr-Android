package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.Vlog
import java.io.Serializable
import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID

data class UserVlogItem(
    val user: UserItem,
    val vlog: VlogItem
) : Serializable {
    val url = with(idList.random()) {
        URL("https://assets.mixkit.co/videos/$this/$this-720.mp4")
    }
}

fun Pair<User, Vlog>.mapToPresentation(): UserVlogItem = UserVlogItem(
    this.first.mapToPresentation(),
    this.second.mapToPresentation()
)

fun List<Pair<User, Vlog>>.mapToPresentation(): List<UserVlogItem> = map { it.mapToPresentation() }
