package com.laixer.navigation.features

import android.content.Intent
import com.laixer.navigation.loadIntentOrNull

object SampleNavigation : DynamicFeature<Intent> {

    const val USER_ID_KEY = "USER_ID_KEY"
    const val VLOG_ID_KEY = "VLOG_ID_KEY"

    private const val VLOG_LIST = "com.laixer.sample.presentation.vloglist.VlogListActivity"
    private const val VLOG_DETAILS = "com.laixer.sample.presentation.vlogdetails.VlogDetailsActivity"

    override val dynamicStart: Intent?
        get() = VLOG_LIST.loadIntentOrNull()

    fun vlogDetails(userId: String, vlogId: String): Intent? =
        VLOG_DETAILS.loadIntentOrNull()
            ?.apply {
                putExtra(USER_ID_KEY, userId)
                putExtra(VLOG_ID_KEY, vlogId)
            }
}
