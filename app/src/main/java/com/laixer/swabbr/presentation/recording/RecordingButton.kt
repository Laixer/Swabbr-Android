package com.laixer.swabbr.presentation.recording

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.setBackgroundTint
import com.laixer.swabbr.extensions.setProgressTint
import com.laixer.swabbr.presentation.types.RecordingButtonState
import com.laixer.swabbr.presentation.types.RecordingButtonState.*
import kotlinx.android.synthetic.main.layout_recording_button.view.*
import java.time.Duration

/**
 *  Custom recording button which implements recording time constraints.
 */
class RecordingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    /**
     *  Minimum recorded video length.
     */
    private var minimumVideoDuration: Duration = defaultMinimumVideoDuration

    /**
     *  Maximum recorded video length.
     */
    private var maximumVideoDuration: Duration = defaultMaximumVideoDuration

    /**
     *  Indicates the state of this recording button.
     */
    private var state: RecordingButtonState = DISABLED

    /**
     *  Inflate using our custom layout.
     */
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_recording_button, this, true)
    }

    /**
     *  Sets the minimum and maximum duration of this video.
     */
    fun setMinMaxDuration(minimumVideoDuration: Duration, maximumVideoDuration: Duration) {
        if (minimumVideoDuration >= maximumVideoDuration) {
            throw IllegalArgumentException("Minimum duration can't be longer than maximum duration")
        }

        this.minimumVideoDuration = minimumVideoDuration
        this.maximumVideoDuration = maximumVideoDuration
    }

    // TODO Kind of ugly with [setOnClickListener].
    // TODO Do we really need to pass the state?
    /**
     *  Sets the click listener for when we click the recording button.
     */
    fun setCustomOnClickListener(callback: (state: RecordingButtonState) -> Unit) {
        recording_button_button?.setOnClickListener { callback.invoke(state) }
    }

    // TODO Ugly, either pass state as param or use observer pattern?
    /**
     *  Called when we update our [state].
     */
    private fun onStateUpdated() {
        // TODO inb4 not on UI thread crash
        // Handle the UI.
        recording_button_button?.isEnabled = when (state) {
            DISABLED -> false
            ENABLED -> true
            RECORDING_BEFORE_MINIMUM_DURATION -> false
            RECORDING_AFTER_MINIMUM_DURATION -> true
        }

        recording_button_button?.setBackgroundTint(
            when (state) {
                RECORDING_BEFORE_MINIMUM_DURATION -> R.color.recordingButtonCenterBefore
                RECORDING_AFTER_MINIMUM_DURATION -> R.color.recordingButtonCenterAfter
                else -> R.color.recordingButtonCenterIdle
            }
        )

        recording_button_outline?.setProgressTint(
            when (state) {
                RECORDING_BEFORE_MINIMUM_DURATION -> R.color.recordingButtonOutlineBefore
                RECORDING_AFTER_MINIMUM_DURATION -> R.color.recordingButtonOutlineAfter
                else -> R.color.recordingButtonOutlineIdle
            }
        )

        // Generic method calls. When more functionality is required this can become a when statement.
        if (state == RECORDING_BEFORE_MINIMUM_DURATION) {
            startProgressBarCountdown()
        }
    }

    /**
     *  Call this method when we are ready to start recording.
     */
    fun setReady() {
        state = ENABLED
        onStateUpdated()
    }

    /**
     *  Call this method when the recording has started.
     */
    fun onStartRecording() {
        state = RECORDING_BEFORE_MINIMUM_DURATION
        onStateUpdated()
    }

    // TODO This is explicitly called but could also be attached to the countdown timer.
    /**
     *  Call this method when the minimum recording time has elapsed.
     */
    fun onMinimumRecordTimeElapsed() {
        state = RECORDING_AFTER_MINIMUM_DURATION
        onStateUpdated()
    }

    /**
     *  Call this method when the button should be disabled.
     */
    fun setDisabled() {
        state = DISABLED
        onStateUpdated()
    }

    /**
     *  Launches a countdown timer to manage the outline progress bar.
     */
    private fun startProgressBarCountdown() {
        recording_button_outline?.progress = 0
        recording_button_outline?.max = OUTLINE_MAX_PROGRESS

        // Apply a custom countdown timer to update the circular outline - then start it.
        object : CountDownTimer(minimumVideoDuration.toMillis(), OUTLINE_UPDATE_INTERVAL_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                recording_button_outline?.progress =
                    ((OUTLINE_MAX_PROGRESS * (minimumVideoDuration.toMillis() - millisUntilFinished))
                        / minimumVideoDuration.toMillis()).toInt()
            }

            // Make sure we hit max progress at the end.
            override fun onFinish() {
                recording_button_outline?.progress = OUTLINE_MAX_PROGRESS
            }
        }.start()
    }

    companion object {
        /**
         *  Default for [minimumVideoDuration].
         */
        val defaultMinimumVideoDuration: Duration = Duration.ofSeconds(1)

        /**
         *  Default for [maximumVideoDuration].
         */
        val defaultMaximumVideoDuration: Duration = Duration.ofSeconds(2)

        private val TAG = this::class.java.simpleName
        private const val OUTLINE_UPDATE_INTERVAL_MILLIS = 20L // 50fps
        private const val OUTLINE_MAX_PROGRESS = 100
    }
}
