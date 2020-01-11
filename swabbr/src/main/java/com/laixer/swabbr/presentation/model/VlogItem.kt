package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.usecase.CombinedUserVlog

data class VlogItem(
    val vlogId: String,
    val userId: String,
    val startDate: String,
    val isLive: Boolean,
    val totalLikes: Int
)

fun CombinedUserVlog.mapToPresentation(): VlogItem = VlogItem(
    vlog.vlogId,
    vlog.userId,
    vlog.startDate,
    vlog.isLive,
    vlog.likes.size
)

fun Vlog.mapToPresentation(): VlogItem =
    VlogItem(
        this.vlogId,
        this.userId,
        this.startDate,
        this.isLive,
        this.likes.size
)

fun List<Vlog>.mapToPresentation(): List<VlogItem> = map { it.mapToPresentation() }
