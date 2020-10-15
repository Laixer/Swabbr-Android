package com.laixer.swabbr.presentation.auth

import android.accounts.Account
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.model.PushNotificationPlatform
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.presentation.model.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class AuthViewModel constructor(
    private val userManager: UserManager,
    private val authUseCase: AuthUseCase
) : ViewModel() {

    val authenticatedUser = MutableLiveData<Resource<AuthUserItem?>>()
    private val compositeDisposable = CompositeDisposable()

    fun get(): Account? = userManager.getCurrentAccount()

    fun login(name: String, password: String, fbToken: String) =
        compositeDisposable.add(authUseCase
            .login(
                LoginItem(
                    name,
                    password,
                    true,
                    PushNotificationPlatform.FCM,
                    fbToken
                ).mapToDomain()
            )
            .doOnSubscribe { authenticatedUser.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    userManager.connect(name, password, it.jwtToken)
                    authenticatedUser.setSuccess(it.mapToPresentation())
                },
                { authenticatedUser.setError(it.message) }
            )
        )

    fun register(registration: RegistrationItem) =
        compositeDisposable.add(authUseCase
            .register(registration.mapToDomain())
            .doOnSubscribe { authenticatedUser.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    userManager.createAccount(registration.email, registration.password, it.jwtToken)
                    authenticatedUser.setSuccess(it.mapToPresentation())
                },
                { authenticatedUser.setError(it.message) }
            )
        )

    fun logout() =
        compositeDisposable.add(authUseCase
            .logout()
            .doOnSubscribe { authenticatedUser.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    userManager.disconnect()
                    authenticatedUser.setSuccess(null)
                },
                { authenticatedUser.setError(it.message) }
            )
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
