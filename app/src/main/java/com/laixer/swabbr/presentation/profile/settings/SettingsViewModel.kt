package com.laixer.swabbr.presentation.profile.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.SettingsUseCase
import com.laixer.swabbr.presentation.model.UserUpdatablePropertiesItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 *  View model for managing user settings. Note that any changes made
 *  to a [UserUpdatablePropertiesItem] are propagated to the backend
 *  when calling [setUpdatableProperties], but that non-set properties
 *  (which remain as [null]) will not be modified in the backend.
 */
class SettingsViewModel constructor(
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {

    val settings = MutableLiveData<Resource<UserUpdatablePropertiesItem>>()
    private val compositeDisposable = CompositeDisposable()

    /**
     *  Gets the updatable properties object from the current user.
     *
     *  @param Force a refresh.
     */
    fun getUpdatableProperties(refresh: Boolean) = compositeDisposable.add(
        settingsUseCase.get(refresh)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { settings.setSuccess(it.mapToPresentation()) },
                { settings.setError(it.message) }
            )
    )

    /**
     *  Sets the modified updated properties. Only those which are
     *  not null will be affected.
     *
     *  @param updatedUserProperties Properties to set.
     */
    fun setUpdatableProperties(updatedUserProperties: UserUpdatablePropertiesItem) = compositeDisposable.add(
        settingsUseCase.set(updatedUserProperties.mapToDomain())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { settings.setSuccess(it.mapToPresentation()) },
                { settings.setError(it.message) }
            )
    )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
