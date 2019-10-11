package com.laixer.sample.presentation.model

import com.laixer.sample.domain.model.Vlog
import com.laixer.sample.domain.usecase.CombinedUserVlog

data class VlogItem(
    val id: String,
    val duration: String,
    val startDate: String,
    val isLive: Boolean,
    val totalViews: Int,
    val totalReactions: Int,
    val totalLikes: Int
)

fun List<CombinedUserVlog>.mapToPresentation(): List<VlogItem> =
    map {
        VlogItem(
            it.vlog.id,
            it.vlog.duration,
            it.vlog.startDate,
            it.vlog.isLive,
            it.vlog.totalViews,
            it.vlog.totalReactions,
            it.vlog.totalLikes
        )
    }

fun Vlog.mapToPresentation(): VlogItem =
    VlogItem(
        this.id,
        this.duration,
        this.startDate,
        this.isLive,
        this.totalViews,
        this.totalReactions,
        this.totalLikes
    )