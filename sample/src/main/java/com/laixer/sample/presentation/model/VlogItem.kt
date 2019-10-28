package com.laixer.sample.presentation.model

import com.laixer.sample.domain.usecase.CombinedUserVlog
import java.io.Serializable

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
) : Serializable

fun VlogItem.getUrlString(): String =
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

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
