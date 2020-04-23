package com.laixer.swabbr.domain.model

import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID

data class Vlog(
    val id: UUID,
    val userId: UUID,
    val isPrivate: Boolean,
    val isLive: Boolean,
    val dateStarted: ZonedDateTime,
    val totalViews: Int,
    val totalReactions: Int,
    val likes: List<Like>,
    val url: URL
)
