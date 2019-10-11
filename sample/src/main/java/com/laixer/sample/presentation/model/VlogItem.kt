package com.laixer.sample.presentation.model

import com.laixer.sample.domain.model.User
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
    val totalLikes: Int,
    val firstName: String,
    val lastName: String,
    val nickname: String,
    val email: String
)

fun CombinedUserVlog.mapToPresentation(): VlogItem = VlogItem(
    vlog.id,
    user.id,
    vlog.duration,
    vlog.startDate,
    vlog.isLive,
    vlog.totalViews,
    vlog.totalReactions,
    vlog.totalLikes,
    user.firstName,
    user.lastName,
    user.nickname,
    user.email
)

fun List<CombinedUserVlog>.mapToPresentation(): List<VlogItem> = map { it.mapToPresentation() }

fun Vlog.mapToPresentation(user: User): VlogItem =
    VlogItem(
        this.id,
        user.id,
        this.duration,
        this.startDate,
        this.isLive,
        this.totalViews,
        this.totalReactions,
        this.totalLikes,
        user.firstName,
        user.lastName,
        user.nickname,
        user.email
    )