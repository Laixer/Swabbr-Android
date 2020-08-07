package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.User
import java.time.ZonedDateTime
import java.util.UUID

data class ReactionItem(
    val id: UUID,
    val userId: UUID,
    val vlogId: UUID,
    val firstname: String?,
    val lastname: String?,
    val nickname: String,
    val profileImage: String?,
    val datePosted: ZonedDateTime
)

fun Pair<User, Reaction>.mapToPresentation(): ReactionItem =
    ReactionItem(
        this.second.id,
        this.first.id,
        this.second.vlogId,
        this.first.firstName,
        this.first.lastName,
        this.first.nickname,
        this.first.profileImage,
        this.second.datePosted
    )

fun List<Pair<User, Reaction>>.mapToPresentation(): List<ReactionItem> = map { it.mapToPresentation() }
