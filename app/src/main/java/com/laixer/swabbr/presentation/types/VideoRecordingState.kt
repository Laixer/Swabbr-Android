package com.laixer.swabbr.presentation.types

/**
 *  Indicates the state of a video recording process.
 */
enum class VideoRecordingState(val value: Int) {
    LOADING(0),
    READY(1),
    SWITCHING_CAMERA(2),
    RECORDING(3),
    DONE_RECORDING(4),
    ERROR(5),
    RECORDING_INTERRUPTED(6)
}
