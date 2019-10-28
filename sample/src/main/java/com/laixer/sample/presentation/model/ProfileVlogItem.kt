package com.laixer.sample.presentation.model

import com.laixer.sample.domain.model.User
import com.laixer.sample.domain.model.Vlog
import java.io.Serializable

data class ProfileVlogItem(
    val userId: String,
    val nickname: String,
    val firstName: String,
    val lastName: String,
    val vlogId: String,
    val duration: String,
    val startDate: String,
    val isLive: Boolean,
    val totalViews: Int,
    val totalReactions: Int,
    val totalLikes: Int
) : Serializable

fun ProfileVlogItem.getUrlString(): String =
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

fun Pair<User, Vlog>.mapToPresentation(): ProfileVlogItem =
    ProfileVlogItem(
        this.first.id,
        this.first.nickname,
        this.first.firstName,
        this.first.lastName,
        this.second.id,
        this.second.duration,
        this.second.startDate,
        this.second.isLive,
        this.second.totalViews,
        this.second.totalReactions,
        this.second.totalLikes
    )

fun List<Pair<User, Vlog>>.mapToPresentation(): List<ProfileVlogItem> = map { it.mapToPresentation() }
