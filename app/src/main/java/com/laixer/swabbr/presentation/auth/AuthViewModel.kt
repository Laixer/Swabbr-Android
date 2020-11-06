package com.laixer.swabbr.presentation.auth

import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.presentation.model.AuthUserItem
import com.laixer.swabbr.presentation.model.RegistrationItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class AuthViewModel constructor(
    private val userManager: UserManager,
    private val authUseCase: AuthUseCase,
    private val firebaseMessaging: FirebaseMessaging
) : ViewModel() {

    val authenticatedUser = MutableLiveData<Resource<AuthUserItem?>>()
    private val compositeDisposable = CompositeDisposable()

    fun login(name: String, password: String, fbToken: String) =
        compositeDisposable.add(authUseCase
            .login(name, password, fbToken)
            .doOnSubscribe { authenticatedUser.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    it.jwtToken?.let { token ->
                        firebaseMessaging.isAutoInitEnabled = true
                        userManager.connect(name, password, token, bundleOf("id" to it.user.id))
                        authenticatedUser.setSuccess(it.mapToPresentation())
                    } ?: authenticatedUser.setError("Auth token is null")
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
                {
                    userManager.disconnect(true)
                    authenticatedUser.setError(it.message)
                }
            )
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}
