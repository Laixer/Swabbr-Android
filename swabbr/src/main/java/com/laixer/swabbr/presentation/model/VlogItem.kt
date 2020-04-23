package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.usecase.CombinedUserVlog
import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID

data class VlogItem(
    val id: UUID,
    val userId: UUID,
    val dateStarted: ZonedDateTime,
    val isLive: Boolean,
    val totalLikes: Int,
    val url: URL
)

fun CombinedUserVlog.mapToPresentation(): VlogItem =
    VlogItem(vlog.id, vlog.userId, vlog.dateStarted, vlog.isLive, vlog.likes.size, vlog.url)

fun Vlog.mapToPresentation(): VlogItem =
    VlogItem(this.id, this.userId, this.dateStarted, this.isLive, this.likes.size, this.url)

fun List<Vlog>.mapToPresentation(): List<VlogItem> = map { it.mapToPresentation() }
