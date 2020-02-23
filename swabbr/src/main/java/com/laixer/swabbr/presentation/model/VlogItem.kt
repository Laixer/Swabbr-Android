package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.usecase.CombinedUserVlog

data class VlogItem(
    val vlogId: String,
    val userId: String,
    val duration: String,
    val startDate: String,
    val isLive: Boolean,
    val totalViews: Int,
    val totalReactions: Int,
    val totalLikes: Int,
    val url: String
)

fun CombinedUserVlog.mapToPresentation(): VlogItem = VlogItem(
    vlog.id,
    vlog.userId,
    vlog.duration,
    vlog.startDate,
    vlog.isLive,
    vlog.totalViews,
    vlog.totalReactions,
    vlog.totalLikes,
    vlog.url
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
        this.totalLikes,
        this.url
    )

fun List<Vlog>.mapToPresentation(): List<VlogItem> = map { it.mapToPresentation() }
