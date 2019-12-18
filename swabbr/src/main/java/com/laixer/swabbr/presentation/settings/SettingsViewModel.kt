package com.laixer.swabbr.presentation.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.SettingsUseCase
import com.laixer.swabbr.presentation.model.SettingsItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SettingsViewModel constructor(
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {

    val settings = MutableLiveData<Resource<SettingsItem?>>()
    private val compositeDisposable = CompositeDisposable()

    fun getSettings(refresh: Boolean) =
        compositeDisposable.add(
            settingsUseCase.get(refresh)
                .subscribeOn(Schedulers.io())
                .subscribe({ settings.setSuccess(it.mapToPresentation()) }, { settings.setError(it.message) })
        )

    fun setSettings(settingsItem: SettingsItem) =
        compositeDisposable.add(
            settingsUseCase.set(settingsItem.mapToDomain())
                .subscribeOn(Schedulers.io())
                .subscribe({ settings.setSuccess(it.mapToPresentation()) }, { settings.setError(it.message) })
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
