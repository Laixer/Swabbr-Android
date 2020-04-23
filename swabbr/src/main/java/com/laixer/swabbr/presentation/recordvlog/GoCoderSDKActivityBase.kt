/**
 * This is sample code provided by Wowza Media Systems, LLC. All sample code is intended to be a reference for the
 * purpose of educating developers, and is not intended to be used in any production environment.
 *
 * IN NO EVENT SHALL WOWZA MEDIA SYSTEMS, LLC BE LIABLE TO YOU OR ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 * OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 * EVEN IF WOWZA MEDIA SYSTEMS, LLC HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * WOWZA MEDIA SYSTEMS, LLC SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. ALL CODE PROVIDED HEREUNDER IS PROVIDED "AS IS".
 * WOWZA MEDIA SYSTEMS, LLC HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 * © 2015 – 2019 Wowza Media Systems, LLC. All rights reserved.
 */
package com.laixer.swabbr.presentation.recordvlog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.laixer.swabbr.BuildConfig
import com.wowza.gocoder.sdk.api.WowzaGoCoder
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcast
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcastAPI
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcastConfig
import com.wowza.gocoder.sdk.api.errors.WOWZStreamingError
import com.wowza.gocoder.sdk.api.logging.WOWZLog
import com.wowza.gocoder.sdk.api.monitor.WOWZStreamingStat
import com.wowza.gocoder.sdk.api.status.WOWZBroadcastStatusCallback
import com.wowza.gocoder.sdk.support.status.WOWZStatusCallback
import kotlinx.android.synthetic.main.activity_record.*

abstract class GoCoderSDKActivityBase : AppCompatActivity(), WOWZStatusCallback {
    private var mPermissionsGranted = false
    protected lateinit var mWZBroadcast: WOWZBroadcast
    protected lateinit var sGoCoderSDK: WowzaGoCoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WOWZLog.LOGGING_ENABLED = true
        mPermissionsGranted = allPermissionsGranted()
        // Initalize the GoCoder SDK
        sGoCoderSDK = WowzaGoCoder.init(this, BuildConfig.GOCODER_SDK_KEY)
        // Make sure GoCoder was successfully initialized
        check(::sGoCoderSDK.isInitialized) { WowzaGoCoder.getLastError().errorDescription }

        mWZBroadcast = WOWZBroadcast()
        mWZBroadcast.logLevel = WOWZLog.LOG_LEVEL_DEBUG
        // Setup adaptive bitrate and framerate listeners
        mWZBroadcast.isABRActivated = true
        mWZBroadcast.registerAdaptiveFrameRateListener(abrListener)
        mWZBroadcast.registerAdaptiveBitRateListener(abrListener)
    }

    override fun onPause() {
        endBroadcast()
        super.onPause()
    }

    protected fun checkPermissionGranted(source: String): Boolean =
        ContextCompat.checkSelfPermission(applicationContext, source) == PackageManager.PERMISSION_GRANTED

    protected fun ifAllPermissionsGranted(callback: () -> Unit) {
        if (mPermissionsGranted) {
            callback()
        } else {
            // Request camera permissions
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE
            )
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    protected fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        checkPermissionGranted(it)
    }

    /**
     * Callback for when a permission request has been processed
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mPermissionsGranted = when (requestCode) {
            PERMISSION_REQUEST_CODE -> grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            else -> false
        }
    }

    /**
     * Enable Android's sticky immersive full-screen mode
     * See http://developer.android.com/training/system-ui/immersive.html#sticky
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
        else showSystemUI()
    }

    private fun hideSystemUI() {
        val rootView: View? = window.decorView.findViewById(android.R.id.content)
        rootView?.systemUiVisibility =
            (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }

    private fun showSystemUI() {
        val rootView: View? = window.decorView.findViewById(android.R.id.content)
        rootView?.systemUiVisibility =
            (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    protected fun startBroadcast(
        config: WOWZBroadcastConfig,
        callback: WOWZBroadcastStatusCallback?
    ): WOWZStreamingError? {
        if (mWZBroadcast.status.isIdle) {
            if (!config.isVideoEnabled && !config.isAudioEnabled) {
                Toast.makeText(
                    this, "Unable to publish if both audio and video are disabled", Toast.LENGTH_LONG
                ).show()
                return WOWZStreamingError(WOWZStreamingError.VIDEO_SOURCE_NOT_SPECIFIED)
            }

            WOWZLog.debug("Scale Mode: ->  ${camera_view.scaleMode}")
            config.isABREnabled = true
            config.frameRateLowBandwidthSkipCount = 1

            if (!config.isAudioEnabled) {
                Toast.makeText(this, "The audio stream is currently turned off", Toast.LENGTH_LONG).show()
            }

            if (!config.isVideoEnabled) {
                Toast.makeText(this, "The video stream is currently turned off", Toast.LENGTH_LONG).show()
            }
            // If config is invalid throw an error
            config.validateForBroadcast()?.let { error ->
                WOWZLog.error(CameraActivityBase.TAG, error.errorDescription)
                return error
            }
            // If config is valid start broadcasting
            mWZBroadcast.startBroadcast(config, callback)
        }
        return null
    }

    private val abrListener = object : WOWZBroadcastAPI.AdaptiveChangeListener {
        override fun adaptiveBitRateChange(broadcastStat: WOWZStreamingStat, newBitrate: Int): Int {
            WOWZLog.debug(TAG, "adaptiveBitRateChange[$newBitrate]")
            mWZBroadcast.broadcastConfig.videoBitRate = newBitrate
            return newBitrate
        }

        override fun adaptiveFrameRateChange(broadcastStat: WOWZStreamingStat, newFrameRate: Int): Int {
            WOWZLog.debug(TAG, "adaptiveFrameRateChange[$newFrameRate]")
            mWZBroadcast.broadcastConfig.videoFramerate = newFrameRate
            return newFrameRate
        }
    }

    protected fun endBroadcast() {
        if (mWZBroadcast.status.isBroadcasting) mWZBroadcast.endBroadcast(this)
    }

    companion object {
        private const val TAG = "GoCoderSDKActivityBase"

        // This is an array of all the permission specified in the manifest
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET
        )

        // This is an arbitrary number used to keep tab of the permission
        // request. Where an app has multiple context for requesting permission,
        // this can help differentiate the different contexts
        private const val PERMISSION_REQUEST_CODE = 0x1
    }
}
