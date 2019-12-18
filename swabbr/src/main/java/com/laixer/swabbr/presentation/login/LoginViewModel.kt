package com.laixer.swabbr.presentation.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.usecase.AuthUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoginViewModel constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    val authorized = MutableLiveData<Resource<Boolean>>()
    private val compositeDisposable = CompositeDisposable()

    fun login(login: Login) =
        compositeDisposable.add(
            authUseCase.login(login)
                .subscribeOn(Schedulers.io())
                .subscribe({ authorized.setSuccess(true) }, { authorized.setError(it.message) })
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
