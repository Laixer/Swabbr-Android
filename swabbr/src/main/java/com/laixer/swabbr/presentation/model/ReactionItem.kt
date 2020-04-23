package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.User
import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID

data class ReactionItem(
    val id: UUID,
    val userId: UUID,
    val vlogId: UUID,
    val nickname: String,
    val profileImageUrl: URL,
    val datePosted: ZonedDateTime
)

fun Pair<User, Reaction>.mapToPresentation(): ReactionItem =
    ReactionItem(
        this.second.id,
        this.first.id,
        this.second.vlogId,
        this.first.nickname,
        this.first.profileImageUrl,
        this.second.datePosted
    )

fun List<Pair<User, Reaction>>.mapToPresentation(): List<ReactionItem> = map { it.mapToPresentation() }
