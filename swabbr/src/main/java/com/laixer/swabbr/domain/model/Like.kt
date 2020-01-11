package com.laixer.swabbr.domain.model

data class Like(
    val vlogLikeId: String,
    val vlogId: String,
    val userId: String,
    val timeCreated: String
)
