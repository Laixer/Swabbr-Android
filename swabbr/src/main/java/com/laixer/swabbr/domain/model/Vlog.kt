package com.laixer.swabbr.domain.model

data class Vlog(
    val userId: String,
    val id: String,
    val duration: String,
    val startDate: String,
    val totalViews: Int,
    val totalReactions: Int,
    val totalLikes: Int,
    val isLive: Boolean,
    val isPrivate: Boolean
) {

    //    when (this.isLive) {
//    true -> "https://wowzaprod270-i.akamaihd.net/hls/live/1003477/7ed632e7/playlist.m3u8"
//    false ->
    val url: String = when (this.id) {
        "101" -> "https://assets.mixkit.co/videos/1178/1178-720.mp4"
        "102" -> "https://assets.mixkit.co/videos/1261/1261-720.mp4"
        "103" -> "https://assets.mixkit.co/videos/1240/1240-720.mp4"
        "104" -> "https://assets.mixkit.co/videos/1164/1164-720.mp4"
        "105" -> "https://assets.mixkit.co/videos/1173/1173-720.mp4"
        "106" -> "https://assets.mixkit.co/videos/2721/2721-720.mp4"
        else -> "https://assets.mixkit.co/videos/1183/1183-720.mp4"
    }
}
