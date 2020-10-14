package com.laixer.swabbr.presentation.auth

import android.accounts.Account
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.presentation.model.AuthUserItem
import com.laixer.swabbr.presentation.model.LoginItem
import com.laixer.swabbr.presentation.model.RegistrationItem
import com.laixer.swabbr.presentation.model.mapToDomain
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class AuthViewModel constructor(
    private val userManager: UserManager,
    private val authUseCase: AuthUseCase
) : ViewModel() {

    val authenticatedUser = MutableLiveData<Resource<AuthUserItem>>()
    private val compositeDisposable = CompositeDisposable()

    fun get(): Account? = userManager.getCurrentAccount()

    fun login(login: LoginItem) =
        compositeDisposable.add(authUseCase
            .login(login.mapToDomain())
            .doOnSubscribe { authenticatedUser.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { userManager.connect(login.email, login.password, it.jwtToken) },
                { authenticatedUser.setError(it.message) }
            )
        )

    fun register(registration: RegistrationItem) =
        compositeDisposable.add(authUseCase
            .register(registration.mapToDomain())
            .doOnSubscribe { authenticatedUser.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { userManager.createAccount(registration.email, registration.password, it.jwtToken) },
                { authenticatedUser.setError(it.message) }
            )
        )

    fun logout() =
        compositeDisposable.add(authUseCase
            .logout()
            .doOnSubscribe { authenticatedUser.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { userManager.disconnect() },
                { authenticatedUser.setError(it.message) }
            )
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
