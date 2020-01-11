package com.laixer.swabbr.domain.model

data class Vlog(
    val vlogId: String,
    val userId: String,
    val isPrivate: Boolean,
    val isLive: Boolean,
    val startDate: String,
    val likes: List<Like>
) {
    val url: String = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
}
