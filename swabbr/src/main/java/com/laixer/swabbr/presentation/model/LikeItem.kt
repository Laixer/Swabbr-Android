package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Like

data class LikeItem(
    val vlogLikeId: String,
    val vlogId: String,
    val userId: String,
    val timeCreated: String
)

fun Like.mapToPresentation(): LikeItem =
    LikeItem(
        this.vlogLikeId,
        this.vlogId,
        this.userId,
        this.timeCreated
    )
