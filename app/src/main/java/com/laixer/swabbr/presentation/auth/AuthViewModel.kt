package com.laixer.swabbr.presentation.auth

import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.model.TokenWrapper
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.domain.usecase.AuthUserUseCase
import com.laixer.swabbr.presentation.model.RegistrationItem
import com.laixer.swabbr.presentation.model.UserCompleteItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

// TODO This is a mess. This contruction of logging in when registering
//      while the backend doesn't bundle these calls might cause problems.
//      Also, look at the blockingGet() calls.
/**
 *  View model for managing user authentication operations.
 *
 *  This also stores the active jwt token. TODO Is that correct? Probably not.
 */
open class AuthViewModel constructor(
    private val userManager: UserManager,
    private val authUserUseCase: AuthUserUseCase,
    private val authUseCase: AuthUseCase,
    private val firebaseMessaging: FirebaseMessaging
) : ViewModel() {

    val authenticatedUser = MutableLiveData<Resource<UserCompleteItem?>>()
    val tokenWrapper = MutableLiveData<Resource<TokenWrapper>>()
    private val compositeDisposable = CompositeDisposable()

    /**
     *  Logs the user in and subscribes for firebase notifications.
     *
     *  @param email User login email.
     *  @param password User password.
     *  @param firebaseToken Firebase token.
     */
    fun login(email: String, password: String, firebaseToken: String) =
        compositeDisposable.add(authUseCase
            .login(email, password, firebaseToken)
            .doOnSubscribe { authenticatedUser.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    // TODO This doesn't check if we actually got a valid token.

                    // Save the token wrapper locally
                    // TODO Do we even need this ever? I don't think so, as the userManager already has the token.
                    tokenWrapper.setSuccess(it)

                    // Trigger the login and user getting procedure.
                    loginGetUser(email, password)
                },
                {
                    authenticatedUser.setError(it.message)
                }
            )
        )

    /**
     *  Register a user and login right after if the registration succeeds.
     *
     *  @param registration The registration object.
     *  @param firebaseToken Firebase token, required for logging in.
     */
    fun register(registration: RegistrationItem, firebaseToken: String) =
        compositeDisposable.add(authUseCase
            .register(registration.mapToDomain())
            .doOnSubscribe { authenticatedUser.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    // First log the user in to acquire the jwt token.
                    tokenWrapper.setSuccess(
                        authUseCase
                            .login(registration.email, registration.password, firebaseToken)
                            .blockingGet()
                    )

                    userManager.createAccount(registration.email, registration.password)

                    // TODO This probably contains duplicate operations with the userManager.createAccount method.
                    loginGetUser(registration.email, registration.password)
                },
                { authenticatedUser.setError(it.message) }
            )
        )

    /**
     *  Logs the user out. Note that this will also disable any
     *  future notifications through firebase.
     */
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

    /**
     *  Connects to the user manager, gets the user from the
     *  API and stores the user in [authenticatedUser].
     */
    private fun loginGetUser(email: String, password: String) {
        // First login using the local user manager to store the jwt token.
        // This has to be done in order for the auth interceptor to work.
        userManager.connect(
            email = email,
            password = password,
            token = tokenWrapper.value?.data?.jwtToken!!,
            extras = bundleOf() // TODO This used to have id, is this correct?
        )

        firebaseMessaging.isAutoInitEnabled = true

        // Then get the user to be stored for later retrieval.
        // Logging in using the API only returns the token wrappers.
        // TODO Is having the user required for this specific class?
        val self = authUserUseCase.getSelf(true)
            .map { self -> self.mapToPresentation() }
            .blockingGet()
        authenticatedUser.setSuccess(self)
    }
}
