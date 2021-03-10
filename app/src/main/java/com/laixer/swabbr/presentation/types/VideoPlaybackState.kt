package com.laixer.swabbr.presentation.types

/**
 *  Indicates the state of a video playback process.
 */
enum class VideoPlaybackState(val value: Int) {
    LOADING(0),
    READY(1),
    PLAYING(2),
    PAUSED(3),
    FINISHED(4),
    ERROR(5)
}
