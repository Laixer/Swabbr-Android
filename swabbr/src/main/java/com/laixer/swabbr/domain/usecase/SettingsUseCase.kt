package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.repository.SettingsRepository
import io.reactivex.Single

class SettingsUseCase constructor(private val settingsRepository: SettingsRepository) {

    fun get(refresh: Boolean): Single<Settings> = settingsRepository.get(refresh)

    fun set(settings: Settings): Single<Settings> = settingsRepository.set(settings)
}
