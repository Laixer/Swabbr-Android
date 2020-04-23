package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Like
import java.time.ZonedDateTime
import java.util.UUID

data class LikeItem(val id: UUID, val vlogId: UUID, val userId: UUID, val timeCreated: ZonedDateTime)

fun Like.mapToPresentation(): LikeItem = LikeItem(this.id, this.vlogId, this.userId, this.timeCreated)
