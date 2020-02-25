package com.laixer.swabbr.presentation.recordvlog

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.laixer.navigation.features.SwabbrNavigation
import com.laixer.swabbr.R
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcastConfig
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig
import com.wowza.gocoder.sdk.api.devices.WOWZAudioDevice
import com.wowza.gocoder.sdk.api.logging.WOWZLog
import com.wowza.gocoder.sdk.api.status.WOWZBroadcastStatus
import com.wowza.gocoder.sdk.api.status.WOWZBroadcastStatusCallback
import com.wowza.gocoder.sdk.support.status.WOWZStatus
import kotlinx.android.synthetic.main.activity_record.*

class RecordVlogActivity : CameraActivityBase(), WOWZBroadcastStatusCallback {

    private var broadcasting = false
    private lateinit var streamConfig: WOWZBroadcastConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val connectionSettings: SwabbrNavigation.ConnectionSettings? =
            intent.getSerializableExtra(SwabbrNavigation.CONNECTION_SETTINGS) as SwabbrNavigation.ConnectionSettings?

        toggle_broadcast.isEnabled = false
        // Check if we received connection credentials
        if (connectionSettings == null) {
            Toast.makeText(
                this,
                resources.getString(R.string.status_error),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        // Create a stream configuration
        streamConfig = WOWZBroadcastConfig(WOWZMediaConfig.FRAME_SIZE_1920x1080)

        streamConfig.connectionParameters
        streamConfig.videoBroadcaster = camera_view
        streamConfig.audioBroadcaster = WOWZAudioDevice()
        streamConfig.cloudCode = connectionSettings.cloudCode
        streamConfig.hostAddress = connectionSettings.hostAddress
        streamConfig.applicationName = connectionSettings.appName
        streamConfig.streamName = connectionSettings.streamName
        streamConfig.portNumber = connectionSettings.port
        // Check if the config is valid
        streamConfig.validateForBroadcast()?.let { error ->
            Toast.makeText(
                this,
                error.errorDescription ?: resources.getString(R.string.status_error),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        toggle_broadcast.setOnClickListener {
            runOnUiThread {
                toggle_broadcast.isEnabled = false
                stopBroadcasting("Stopped broadcasting")
            }
        }
    }

    override fun onWZStatus(status: WOWZBroadcastStatus) {
        runOnUiThread {
            when (status.state) {
                WOWZBroadcastStatus.BroadcastState.BROADCASTING -> {
                    // Keep the screen on while the broadcast is active
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                    broadcasting = true

                    Toast.makeText(
                        this,
                        "Started broadcasting" +
                            "\n${mWZBroadcast.broadcastConfig.getLabel(
                                true,
                                true,
                                true,
                                true
                            )}",
                        Toast.LENGTH_LONG
                    ).show()

                    timer_view.addEventAt(
                        DEFAULT_MINIMUM_RECORD_TIME_MINUTES,
                        DEFAULT_MINIMUM_RECORD_TIME_SECONDS
                    ) {
                        // Allow for broadcast to be stopped
                        toggle_broadcast.isEnabled = true
                    }

                    timer_view.addEventAt(
                        DEFAULT_MAXIMUM_RECORD_TIME_MINUTES,
                        DEFAULT_MAXIMUM_RECORD_TIME_SECONDS
                    ) {
                        stopBroadcasting("Time limit reached, stopping broadcast.")
                    }
                    // Start the timer when the user starts streaming
                    timer_view.startTimer()
                }
                WOWZBroadcastStatus.BroadcastState.IDLE -> {
                    // Clear the "keep screen on" flag
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    timer_view.stopTimer()
                    broadcasting = false
                }
                WOWZBroadcastStatus.BroadcastState.READY -> {
                }
                else -> {
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startBroadcasting()
    }

    override fun onWZError(p0: WOWZStatus?) {
        return
    }

    override fun onWZError(status: WOWZBroadcastStatus?) {
        stopBroadcasting(status?.lastError?.errorDescription)
    }

    private fun stopBroadcasting(reason: String?) {
        super.endBroadcast()
        runOnUiThread {
            toggle_broadcast.isEnabled = false
            reason?.let {
                Toast.makeText(this, reason, Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun startBroadcasting() {
        countDownFrom(COUNTDOWN_MILLISECONDS) {
            // Start broadcasting
            super.startBroadcast(streamConfig, this)?.let { error ->
                // If broadcasting returns an error, log it
                WOWZLog.error(error)
                Toast.makeText(this, error.errorDescription, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun countDownFrom(countdownMs: Long, onFinish: () -> Unit) {
        countdown.visibility = View.VISIBLE
        val timer =
            object : CountDownTimer(countdownMs, COUNTDOWN_INTERVAL_MILLISECONDS) {
                override fun onTick(millisUntilFinished: Long) {
                    countdown.text = String.format("%1d", (millisUntilFinished / COUNTDOWN_INTERVAL_MILLISECONDS) + 1)
                }

                override fun onFinish() {
                    countdown.visibility = View.INVISIBLE
                    runOnUiThread(onFinish)
                }
            }
        timer.start()
    }

    companion object {
        private const val COUNTDOWN_MILLISECONDS = 3000L
        private const val COUNTDOWN_INTERVAL_MILLISECONDS = 1000L
        private const val DEFAULT_MINIMUM_RECORD_TIME_SECONDS = 8
        private const val DEFAULT_MINIMUM_RECORD_TIME_MINUTES = 0
        private const val DEFAULT_MAXIMUM_RECORD_TIME_SECONDS = 0
        private const val DEFAULT_MAXIMUM_RECORD_TIME_MINUTES = 30
    }
}
