package com.laixer.sample.presentation.model

import com.laixer.sample.domain.usecase.CombinedUserReaction

data class ReactionItem(
    val userId: String,
    val vlogId: String,
    val id: String,
    val nickname: String,
    val duration: String,
    val postDate: String
)

fun List<CombinedUserReaction>.mapToPresentation(): List<ReactionItem> =
    map { ReactionItem(it.user.id, it.reaction.vlogId, it.reaction.id, it.user.nickname,  it.reaction.duration, it.reaction.postDate) }
