package com.laixer.sample.presentation.model

import com.laixer.sample.domain.model.Reaction
import com.laixer.sample.domain.model.User

data class ReactionItem(
    val userId: String,
    val vlogId: String,
    val id: String,
    val nickname: String,
    val duration: String,
    val postDate: String
)

fun List<Pair<User, Reaction>>.mapToPresentation(): List<ReactionItem> =
    map {
        ReactionItem(
            it.first.id,
            it.second.vlogId,
            it.second.id,
            it.first.nickname,
            it.second.duration,
            it.second.postDate
        )
    }
