package com.laixer.navigation.features

import android.content.Intent
import com.laixer.navigation.loadIntentOrNull

object CameraNavigation : DynamicFeature<Intent> {

    private const val RECORD_VLOG = "com.laixer.swabbr.presentation.recordvlog.RecordVlogActivity"

    override val dynamicStart: Intent?
        get() = RECORD_VLOG.loadIntentOrNull()
}
