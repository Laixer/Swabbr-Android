package com.laixer.swabbr.domain.model

data class Reaction(
    val userId: String,
    val vlogId: String,
    val id: String,
    val duration: String,
    val postDate: String
)