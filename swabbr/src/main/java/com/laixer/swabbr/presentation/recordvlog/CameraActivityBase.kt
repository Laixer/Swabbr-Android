package com.laixer.swabbr.presentation.recordvlog

import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import com.laixer.swabbr.R
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig
import com.wowza.gocoder.sdk.api.devices.WOWZCamera
import com.wowza.gocoder.sdk.api.devices.WOWZCameraView
import com.wowza.gocoder.sdk.api.errors.WOWZError
import com.wowza.gocoder.sdk.api.geometry.WOWZSize
import com.wowza.gocoder.sdk.api.graphics.WOWZColor
import com.wowza.gocoder.sdk.api.logging.WOWZLog
import com.wowza.gocoder.sdk.api.status.WOWZBroadcastStatusCallback
import com.wowza.gocoder.sdk.support.status.WOWZStatus

import kotlinx.android.synthetic.main.activity_record.*
import java.util.*

abstract class CameraActivityBase : GoCoderSDKActivityBase(), WOWZCameraView.PreviewStatusListener,
    WOWZBroadcastStatusCallback {

    protected var autoFocusDetector: GestureDetectorCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        // Set mirror capability
        // camera_view.setSurfaceExtension(WOWZCameraView.EXTENSION_MIRROR)

        camera_view.scaleMode = WOWZMediaConfig.FILL_VIEW
        camera_view.videoBackgroundColor = WOWZColor.DARKGREY

        if (camera_view.cameras.isEmpty()) {
            Toast.makeText(
                this,
                resources.getString(R.string.no_devices_available),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        toggle_torch.setOnClickListener {
            camera_view.camera?.isTorchOn = toggle_torch.toggleState()
        }

        toggle_camera.setOnClickListener {
            // Switch camera and enable continuous focus
            val camera = camera_view.switchCamera()
            setContinuousFocus(camera)
            setTorch(camera)
        }
    }

    /**
     * Sets torch and UI state
     */
    private fun setTorch(camera: WOWZCamera?) {
        camera?.let {
            toggle_torch.isOn = true
            toggle_torch.isEnabled = false
            if (camera.hasCapability(WOWZCamera.TORCH)) {
                toggle_torch.isOn = camera.isTorchOn
                toggle_torch.isEnabled = true
            }
        }
    }

    /**
     * Enables continuous focus on a camera if it has the capability
     */
    private fun setContinuousFocus(camera: WOWZCamera?) {
        camera?.let {
            if (camera.hasCapability(WOWZCamera.FOCUS_MODE_CONTINUOUS)) {
                camera.focusMode = WOWZCamera.FOCUS_MODE_CONTINUOUS
            }
        }
    }

    override fun onWZCameraPreviewError(wzCamera: WOWZCamera, wzError: WOWZError?) {
        Toast.makeText(this, wzError.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onWZCameraPreviewStarted(wzCamera: WOWZCamera, wSize: WOWZSize, i: Int) { return }
    override fun onWZCameraPreviewStopped(p0: Int) { return }

    override fun onResume() {
        super.onResume()

        // Check if we have permissions
        ifAllPermissionsGranted {
            camera_view?.let {
                Handler().postDelayed({
                    if (it.isPreviewPaused)
                        it.onResume()
                    else
                        it.startPreview(this) // Apply the configuration to the broadcaster and start previewing

                    if (autoFocusDetector == null) autoFocusDetector =
                        GestureDetectorCompat(this, AutoFocusListener(this, camera_view))

                    // Enable continuous focus on the initial camera
                    setContinuousFocus(it.camera)
                    setTorch(it.camera)
                }, DEFAULT_POST_DELAY)
            }
        }
    }

    override fun onWZStatus(status: WOWZStatus?) {
        WOWZLog.debug("BroadcastStateMachine[CameraActivityBase] : onWZStatus : $status")
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        autoFocusDetector?.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
    override fun onPause() {
        super.onPause()
        camera_view.clearView()
        camera_view.stopPreview()
    }

    companion object {
        const val TAG = "CameraActivityBase"
        const val DEFAULT_POST_DELAY = 300L
    }
}
