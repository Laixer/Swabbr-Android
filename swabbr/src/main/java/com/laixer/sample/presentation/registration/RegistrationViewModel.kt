package com.laixer.swabbr.presentation.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.usecase.AuthUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RegistrationViewModel constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    val authorized = MutableLiveData<Resource<Boolean>>()
    private val compositeDisposable = CompositeDisposable()

    fun register(registration: Registration) =
        compositeDisposable.add(
            authUseCase.register(registration)
                .subscribeOn(Schedulers.io())
                .subscribe({ authorized.setSuccess(true) }, { authorized.setError(it.message) })
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
