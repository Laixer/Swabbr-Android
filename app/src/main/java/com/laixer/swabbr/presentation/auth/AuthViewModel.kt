package com.laixer.swabbr.presentation.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.messaging.FirebaseMessaging
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.presentation.abstraction.ViewModelBase
import com.laixer.swabbr.presentation.model.RegistrationItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.utils.todosortme.setError
import com.laixer.swabbr.presentation.utils.todosortme.setLoading
import com.laixer.swabbr.presentation.utils.todosortme.setSuccess
import com.laixer.swabbr.services.uploading.ReactionUploadWorker
import com.laixer.swabbr.services.uploading.VlogUploadWorker
import com.laixer.swabbr.services.users.UserManager
import com.laixer.swabbr.utils.resources.Resource
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
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
) : ViewModelBase() {

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
        viewModelScope.launch {
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
                                    Log.e(TAG, "Could not login - ${it.message}")
                                }
                            )
                    },
                        {
                            authenticationResultResource.setError(it.message)
                            Log.e(TAG, "Could not login - ${it.message}")
                        })
            )
        }

    /**
     *  Register a user and login right after if the registration succeeds.
     *
     *  @param registration The registration object.
     */
    fun register(registration: RegistrationItem) =
        viewModelScope.launch {
            compositeDisposable.add(authUseCase
                .register(registration.mapToDomain())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        // If we reach this point we have created the user. Call the
                        // login functionality right away so we can log the user in.
                        login(registration.email, registration.password)
                    },
                    {
                        authenticationResultResource.setError(it.message)
                        Log.e(TAG, "Could not register- ${it.message}")
                    }
                )
            )
        }

    /**
     *  Logs the user out. Note that this will also disable any
     *  future notifications through firebase.
     */
    fun logout(context: Context) {
        // Scoped function to also cancel existing jobs.
        fun onLogout() {
            // First cancel, then logout. These jobs expect us to be logged in.
            WorkManager.getInstance(context).cancelAllWorkByTag(ReactionUploadWorker.WORK_TAG)
            WorkManager.getInstance(context).cancelAllWorkByTag(VlogUploadWorker.WORK_TAG)

            userManager.logout()
        }

        viewModelScope.launch {
            compositeDisposable.add(authUseCase
                .logout()
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { onLogout() },
                    {
                        onLogout()
                        Log.e(TAG, "Could not logout properly - ${it.message}")
                    }
                )
            )
        }
    }

    companion object {
        private val TAG = AuthViewModel::class.java.simpleName
    }
}
