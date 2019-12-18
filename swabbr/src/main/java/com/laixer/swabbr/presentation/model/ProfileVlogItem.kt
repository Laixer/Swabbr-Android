package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.Vlog
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

fun ProfileVlogItem.getUrlString(): String = when (this.isLive) {
    true -> "https://wowzaprod270-i.akamaihd.net/hls/live/1003477/7ed632e7/playlist.m3u8"
    false -> "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
}

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
