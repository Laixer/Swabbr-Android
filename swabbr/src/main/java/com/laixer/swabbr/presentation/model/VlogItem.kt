package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.usecase.CombinedUserVlog

data class VlogItem(
    val vlogId: String,
    val userId: String,
    val startDate: String,
    val isLive: Boolean,
    val totalLikes: Int,
    val url: String
)

fun CombinedUserVlog.mapToPresentation(): VlogItem = VlogItem(
    vlog.id,
    vlog.userId,
    vlog.startDate,
    vlog.isLive,
    vlog.likes.size,
    vlog.url
)

fun Vlog.mapToPresentation(): VlogItem =
    VlogItem(
        this.id,
        this.userId,
        this.startDate,
        this.isLive,
        this.likes.size,
        this.url
    )

fun List<Vlog>.mapToPresentation(): List<VlogItem> = map { it.mapToPresentation() }
