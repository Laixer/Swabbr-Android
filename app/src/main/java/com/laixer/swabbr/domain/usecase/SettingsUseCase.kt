package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.repository.AuthRepository
import com.laixer.swabbr.domain.repository.SettingsRepository
import io.reactivex.Single

class SettingsUseCase constructor(
    private val repository: SettingsRepository
) {

    fun get(refresh: Boolean): Single<Settings> = repository.get(refresh)

    fun set(settings: Settings): Single<Settings> = repository.set(settings)
}
