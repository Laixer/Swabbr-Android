package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.usecase.CombinedUserVlog
import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID

data class VlogItem(
    val id: UUID,
    val userId: UUID,
    val url: URL,
    val isPrivate: Boolean,
    val dateStarted: ZonedDateTime
)

fun CombinedUserVlog.mapToPresentation(): VlogItem =
    VlogItem(vlog.id, vlog.userId, vlog.url, vlog.isPrivate, vlog.dateStarted)

fun Vlog.mapToPresentation(): VlogItem =
    VlogItem(this.id, this.userId, this.url, this.isPrivate, this.dateStarted)

fun List<Vlog>.mapToPresentation(): List<VlogItem> = map { it.mapToPresentation() }
