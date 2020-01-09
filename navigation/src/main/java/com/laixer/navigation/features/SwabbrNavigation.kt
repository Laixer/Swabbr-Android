package com.laixer.navigation.features

import android.content.Intent
import com.laixer.navigation.loadIntentOrNull
import java.io.Serializable

object SwabbrNavigation : DynamicFeature<Intent> {

    const val VLOG_ID_KEYS = "VLOG_ID_KEYS"
    const val USER_ID_KEY = "VLOG_ID_KEY"
    const val CONNECTION_SETTINGS = "CONNECTION_SETTINGS"

    private const val LOGIN = "com.laixer.swabbr.presentation.login.LoginActivity"
    private const val REGISTRATION = "com.laixer.swabbr.presentation.registration.RegistrationActivity"
    private const val PROFILE = "com.laixer.swabbr.presentation.profile.ProfileActivity"
    private const val VLOG_LIST = "com.laixer.swabbr.presentation.vloglist.VlogListActivity"
    private const val VLOG_DETAILS =
        "com.laixer.swabbr.presentation.vlogdetails.VlogDetailsActivity"
    private const val SEARCH = "com.laixer.swabbr.presentation.search.SearchActivity"
    private const val RECORD = "com.laixer.swabbr.presentation.recordvlog.RecordVlogActivity"

    private const val SETTINGS = "com.laixer.swabbr.presentation.settings.SettingsActivity"

    override val dynamicStart: Intent?
        get() = LOGIN.loadIntentOrNull()

    fun login(): Intent? =
        LOGIN.loadIntentOrNull()

    fun registration(): Intent? =
        REGISTRATION.loadIntentOrNull()

    fun vlogList(): Intent? =
        VLOG_LIST.loadIntentOrNull()

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

    fun record(settings: ConnectionSettings): Intent? = RECORD.loadIntentOrNull()
        ?.apply {
            putExtra(CONNECTION_SETTINGS, settings)
        }

    data class ConnectionSettings(
        val cloudCode: String?,
        val hostAddress: String,
        val appName: String,
        val streamName: String,
        val port: Int
    ) : Serializable

    fun settings(): Intent? =
        SETTINGS.loadIntentOrNull()

    fun search(): Intent? =
        SEARCH.loadIntentOrNull()
}
