package com.laixer.swabbr.presentation.vlogs.recording

import android.os.Bundle
import android.view.View
import com.laixer.swabbr.presentation.recording.RecordMinMaxVideoFragment
import java.time.Duration

/**
 *  Used to record vlogs.
 */
class RecordVlogFragmentNew : RecordMinMaxVideoFragment() {
    /**
     *  Assign constraints on creation.
     */
    init {
        setMinMaxDuration(vlogMinimumRecordingTime, vlogMaximumRecordingTime)
    }

    companion object {
        // TODO From config maybe?
        /**
         *  Minimum vlog recording time.
         */
        val vlogMinimumRecordingTime: Duration = Duration.ofSeconds(10)

        /**
         *  Maximum vlog recording time.
         */
        val vlogMaximumRecordingTime: Duration = Duration.ofMinutes(10)
    }
}
