package com.laixer.swabbr.presentation.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.messaging.FirebaseMessaging
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.presentation.model.RegistrationItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.services.users.UserManager
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *  View model for managing user login, logout and registration.
 *  Note that this relies on the [UserManager]. This view model
 *  triggers our API calls with regards to authentication, but
 *  the refreshing of any tokens is beyond it's scope. This is
 *  handled by our [UserManager].
 */
open class AuthViewModel constructor(
    private val userManager: UserManager,
    private val authUseCase: AuthUseCase,
    private val firebaseMessaging: FirebaseMessaging
) : ViewModel() {
    // Used for graceful resource disposal.
    private val compositeDisposable = CompositeDisposable()

    /**
     *  Observe this resource to check for when our authenticator
     *  fails to refresh the user auth for whatever reason. If this
     *  is the case, the value in this mutable resource is true. In
     *  this case we need the user to log in again.
     */
    fun getNewAuthenticationRequiredResource() = userManager.newAuthenticationRequiredResource

    /**
     *  Will be updated after we have either logged in or registered. The
     *  boolean value will indicate if the operation was successful or not.
     */
    val authenticationResultResource = MutableLiveData<Resource<Boolean>>()

    /**
     *  Checks if we are authenticated.
     */
    fun isAuthenticated(): Boolean = userManager.hasValidToken()
        || (userManager.hasToken() && userManager.hasValidRefreshToken())

    /**
     *  Returns the user id of the currently authenticated user, or
     *  null if we don't have an authenticated user.
     */
    fun getSelfIdOrNull(): UUID? = userManager.getUserIdOrNull()

    /**
     *  Gets the cached user account name if we have one.
     */
    fun getCachedEmailOrNull(): String? = userManager.getCachedEmailOrNull()

    // TODO Optimize firebase token get operation. Is this the way to go? Maybe it is though...
    /**
     *  Logs the user in. Note that this first gets the firebase
     *  token, then performs the login call.
     *
     *  @param email User login email.
     *  @param password User password.
     */
    fun login(email: String, password: String) =
        compositeDisposable.add(
            Single.fromCallable { await(firebaseMessaging.token) }
                .subscribeOn(Schedulers.io())
                .subscribe({ fbToken ->
                    authUseCase
                        .login(
                            name = email,
                            password = password,
                            fbToken = fbToken
                        )
                        .doOnSubscribe { authenticationResultResource.setLoading() }
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                            {
                                userManager.login(
                                    email = email,
                                    tokenWrapper = it
                                )

                                // Save the token wrapper locally to notify observers.
                                authenticationResultResource.setSuccess(true)
                            },
                            {
                                authenticationResultResource.setError(it.message)
                            }
                        )
                },
                    { authenticationResultResource.setError(it.message) })
        )

    /**
     *  Register a user and login right after if the registration succeeds.
     *
     *  @param registration The registration object.
     */
    fun register(registration: RegistrationItem) =
        compositeDisposable.add(authUseCase
            .register(registration.mapToDomain())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    // If we reach this point we have created the user. Call the
                    // login functionality right away so we can log the user in.
                    login(registration.email, registration.password)
                },
                { authenticationResultResource.setError(it.message) }
            )
        )

    /**
     *  Logs the user out. Note that this will also disable any
     *  future notifications through firebase.
     */
    fun logout() =
        compositeDisposable.add(authUseCase
            .logout()
            .subscribeOn(Schedulers.io())
            .subscribe(
                { userManager.logout() },
                { userManager.logout() }
            )
        )

    /**
     *  Dispose resources.
     */
    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
