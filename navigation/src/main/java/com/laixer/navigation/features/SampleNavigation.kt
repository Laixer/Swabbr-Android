package com.laixer.navigation.features

import android.content.Intent
import com.laixer.navigation.loadIntentOrNull

object SampleNavigation : DynamicFeature<Intent> {

    const val VLOG_ID_KEYS = "VLOG_ID_KEYS"
    const val USER_ID_KEY = "VLOG_ID_KEY"

    private const val PROFILE = "com.laixer.swabbr.presentation.profile.ProfileActivity"
    private const val VLOG_LIST = "com.laixer.swabbr.presentation.vloglist.VlogListActivity"
    private const val VLOG_DETAILS = "com.laixer.swabbr.presentation.vlogdetails.VlogDetailsActivity"
    private const val SEARCH = "com.laixer.swabbr.presentation.search.SearchActivity"

    override val dynamicStart: Intent?
        get() = VLOG_LIST.loadIntentOrNull()

    fun vlogDetails(vlogIds: ArrayList<String>): Intent? =
        VLOG_DETAILS.loadIntentOrNull()
            ?.apply {
                putExtra(VLOG_ID_KEYS, vlogIds)
            }

    fun profile(userId: String): Intent? =
        PROFILE.loadIntentOrNull()
            ?.apply {
                putExtra(USER_ID_KEY, userId)
            }

    val search: Intent? = SEARCH.loadIntentOrNull()
}
