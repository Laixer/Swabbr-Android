package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.repository.AuthRepository
import io.reactivex.Single

class SettingsUseCase constructor(
    private val authRepository: AuthRepository
) {

    fun get(): Single<Settings> = authRepository.getSettings()

    fun set(settings: Settings): Single<Settings> = authRepository.saveSettings(settings)
}
