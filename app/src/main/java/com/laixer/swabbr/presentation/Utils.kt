package com.laixer.swabbr.presentation

import android.app.Activity
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_app.*

object Utils {

    fun enterFullscreen(activity: Activity) = with(activity) {
        toolbar.visibility = View.GONE
        bottom_nav.visibility = View.GONE

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    fun exitFullscreen(activity: Activity) = with(activity) {
        toolbar.visibility = View.VISIBLE
        bottom_nav.visibility = View.VISIBLE

        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_FULLSCREEN)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }
}
