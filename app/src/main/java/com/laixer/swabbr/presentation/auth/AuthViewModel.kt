package com.laixer.swabbr.presentation.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.presentation.model.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.lang.Exception

class AuthViewModel constructor(private val authUseCase: AuthUseCase) : ViewModel() {

    val authenticatedUser = MutableLiveData<Resource<AuthUserItem?>>()
    private val compositeDisposable = CompositeDisposable()

    fun isLoggedIn(): Boolean = authenticatedUser.value?.data?.hasValidSession() ?: false

    fun get(refresh: Boolean = false) =
        compositeDisposable.add(authUseCase
            .getAuthenticatedUser(refresh)
            .doOnSubscribe { authenticatedUser.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { authenticatedUser.setSuccess(it.mapToPresentation()) },
                { authenticatedUser.setError(it.message) }
            )
        )

    fun login(login: LoginItem, remember: Boolean = true) =
        compositeDisposable.add(authUseCase
            .login(login.mapToDomain(), remember)
            .doOnSubscribe { authenticatedUser.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { authenticatedUser.setSuccess(it.mapToPresentation()) },
                { authenticatedUser.setError(it.message) }
            )
        )

    fun register(registration: RegistrationItem, remember: Boolean = true) =
        compositeDisposable.add(authUseCase
            .register(registration.mapToDomain(), remember)
            .doOnSubscribe { authenticatedUser.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { authenticatedUser.setSuccess(it.mapToPresentation()) },
                { authenticatedUser.setError(it.message) }
            )
        )

    fun logout() =
        compositeDisposable.add(authUseCase
            .logout()
            .doOnSubscribe { authenticatedUser.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { authenticatedUser.setSuccess(null) },
                { authenticatedUser.setError(it.message) }
            )
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}