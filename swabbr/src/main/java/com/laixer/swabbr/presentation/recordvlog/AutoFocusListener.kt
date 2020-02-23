package com.laixer.swabbr.presentation.recordvlog

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import com.wowza.gocoder.sdk.api.devices.WOWZCamera
import com.wowza.gocoder.sdk.api.devices.WOWZCameraView

class AutoFocusListener(context: Context, cameraView: WOWZCameraView) :
    GestureDetector.SimpleOnGestureListener() {

    private var mCameraView: WOWZCameraView? = null
    private var mContext: Context? = null

    init {
        this.mCameraView = cameraView
        this.mContext = context
    }

    override fun onDown(e: MotionEvent?): Boolean = true

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        mCameraView?.camera?.let {
            if (it.hasCapability(WOWZCamera.FOCUS_MODE_CONTINUOUS) &&
                it.focusMode != WOWZCamera.FOCUS_MODE_CONTINUOUS
            ) {
                it.focusMode = WOWZCamera.FOCUS_MODE_CONTINUOUS
                Toast.makeText(mContext, "Continuous video focus on", Toast.LENGTH_SHORT).show()
            } else {
                it.focusMode = WOWZCamera.FOCUS_MODE_OFF
                Toast.makeText(mContext, "Continuous video focus off", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        mCameraView?.camera?.let {
            if (it.hasCapability(WOWZCamera.FOCUS_MODE_AUTO)) {
                Toast.makeText(mContext, "Auto focusing at (${e.x}, ${e.y})", Toast.LENGTH_SHORT)
                    .show()
                it.setFocusPoint(e.x, e.y, DEFAULT_FOCUS_AREA_SIZE)
            }
        }
        return true
    }

    companion object {
        const val DEFAULT_FOCUS_AREA_SIZE = 25
    }
}
