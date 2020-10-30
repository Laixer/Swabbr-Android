package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.UploadReaction
import com.laixer.swabbr.domain.model.User
import java.time.ZonedDateTime
import java.util.UUID

data class ReactionItem(
    val id: UUID,
    val userId: UUID,
    val targetVlogId: UUID,
    val createDate: ZonedDateTime,
    val isPrivate: Boolean
)

data class UploadReactionItem(
    val reaction: ReactionItem,
    val uploadUrl: String
)

data class ReactionUserItem(
    val firstname: String?,
    val lastname: String?,
    val nickname: String,
    val profileImage: String?,
    val id: UUID,
    val userId: UUID,
    val targetVlogId: UUID,
    val createDate: ZonedDateTime,
    val isPrivate: Boolean
)

fun Pair<User, Reaction>.mapToPresentation(): ReactionUserItem =
    ReactionUserItem(
        this.first.firstName,
        this.first.lastName,
        this.first.nickname,
        this.first.profileImage,
        this.second.id,
        this.first.id,
        this.second.targetVlogId,
        this.second.createDate,
        this.second.isPrivate
    )


fun UploadReaction.mapToPresentation(): UploadReactionItem = UploadReactionItem(reaction.mapToPresentation(), uploadUrl)
fun Reaction.mapToPresentation(): ReactionItem = ReactionItem(id, userId, targetVlogId, createDate, isPrivate)
fun List<Pair<User, Reaction>>.mapToPresentation(): List<ReactionUserItem> = map { it.mapToPresentation() }
