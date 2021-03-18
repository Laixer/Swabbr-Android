package com.laixer.swabbr.presentation.types

/**
 *  Indicates the state of a video recording process.
 */
enum class VideoRecordingState(val value: Int) {
    LOADING(0),
    UI_READY(1),
    INITIALIZING_CAMERA(2),
    READY(3),
    RECORDING(4),
    DONE_RECORDING(5),
    RECORDING_INTERRUPTED(6),
    ERROR(7)
}
