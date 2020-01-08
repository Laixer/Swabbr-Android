package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.Settings
import io.reactivex.Single

interface SettingsRepository {

    fun get(refresh: Boolean): Single<Settings>

    fun set(settings: Settings, updateRemote: Boolean = true): Single<Settings>
}
