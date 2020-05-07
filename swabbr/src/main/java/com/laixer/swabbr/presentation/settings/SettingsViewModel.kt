package com.laixer.swabbr.presentation.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.domain.usecase.SettingsUseCase
import com.laixer.swabbr.presentation.model.SettingsItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers

class SettingsViewModel constructor(
    private val settingsUseCase: SettingsUseCase,
    private val authUseCase: AuthUseCase
) : ViewModel() {

    val settings = MutableLiveData<Resource<SettingsItem>>()
    val logout = MutableLiveData<Resource<String>>()
    private val compositeDisposable = CompositeDisposable()

    fun getSettings(refresh: Boolean) = compositeDisposable.add(
        settingsUseCase.get()
            .subscribeOn(Schedulers.io())
            .subscribe(
                { settings.setSuccess(it.mapToPresentation()) },
                { settings.setError(it.message) }
            )
    )

    fun setSettings(settingsItem: SettingsItem) = compositeDisposable.add(
        settingsUseCase.set(settingsItem.mapToDomain())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { settings.setSuccess(it.mapToPresentation()) },
                { settings.setError(it.message) }
            )
    )

    fun logout() = compositeDisposable.add(
        authUseCase
            .logout()
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onStart() {
                    logout.setLoading()
                }

                override fun onError(e: Throwable) {
                    logout.setError(e.message)
                }

                override fun onComplete() {
                    logout.setSuccess("Logged out")
                }
            })
    )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
