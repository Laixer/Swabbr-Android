package com.laixer.sample.presentation.model

import com.laixer.sample.domain.model.Vlog
import com.laixer.sample.domain.usecase.CombinedUserVlog

data class VlogItem(
    val vlogId: String,
    val userId: String,
    val duration: String,
    val startDate: String,
    val isLive: Boolean,
    val totalViews: Int,
    val totalReactions: Int,
    val totalLikes: Int
)

fun CombinedUserVlog.mapToPresentation(): VlogItem = VlogItem(
    vlog.id,
    vlog.userId,
    vlog.duration,
    vlog.startDate,
    vlog.isLive,
    vlog.totalViews,
    vlog.totalReactions,
    vlog.totalLikes
)

fun Vlog.mapToPresentation(): VlogItem =
    VlogItem(
        this.id,
        this.userId,
        this.duration,
        this.startDate,
        this.isLive,
        this.totalViews,
        this.totalReactions,
        this.totalLikes
    )

fun List<Vlog>.mapToPresentation(): List<VlogItem> = map { it.mapToPresentation() }
