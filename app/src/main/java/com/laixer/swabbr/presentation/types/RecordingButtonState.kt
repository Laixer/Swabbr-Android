package com.laixer.swabbr.presentation.types

/**
 *  Indicates the state of a video playback process.
 */
enum class RecordingButtonState(val value: Int) {
    DISABLED(0),
    ENABLED(1),
    RECORDING_BEFORE_MINIMUM_DURATION(2),
    RECORDING_AFTER_MINIMUM_DURATION(3),
}
