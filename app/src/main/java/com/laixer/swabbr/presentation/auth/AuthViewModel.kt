package com.laixer.swabbr.presentation.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.messaging.FirebaseMessaging
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.domain.usecase.AuthUserUseCase
import com.laixer.swabbr.presentation.abstraction.ViewModelBase
import com.laixer.swabbr.presentation.model.RegistrationItem
import com.laixer.swabbr.presentation.model.extractUpdatableProperties
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.model.mapToPresentation
import com.laixer.swabbr.presentation.utils.todosortme.setError
import com.laixer.swabbr.presentation.utils.todosortme.setLoading
import com.laixer.swabbr.presentation.utils.todosortme.setSuccess
import com.laixer.swabbr.services.uploading.ReactionUploadWorker
import com.laixer.swabbr.services.uploading.VlogUploadWorker
import com.laixer.swabbr.services.users.UserService
import com.laixer.swabbr.utils.resources.Resource
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

/**
 *  View model for managing user login, logout and registration.
 *  Note that this relies on the [UserService]. This view model
 *  triggers our API calls with regards to authentication, but
 *  the refreshing of any tokens is beyond it's scope. This is
 *  handled by our [UserService].
 */
open class AuthViewModel constructor(
    private val userService: UserService,
    private val authUseCase: AuthUseCase,
    private val authUserUseCase: AuthUserUseCase, // Used for profile image at registration.
    private val firebaseMessaging: FirebaseMessaging
) : ViewModelBase() {

    /**
     *  Observe this resource to check for when our authenticator
     *  fails to refresh the user auth for whatever reason. If this
     *  is the case, the value in this mutable resource is true. In
     *  this case we need the user to log in again.
     */
    fun getNewAuthenticationRequiredResource() = userService.newAuthenticationRequiredResource

    /**
     *  Will be updated after we have either logged in or registered. The
     *  boolean value will indicate if the operation was successful or not.
     */
    val authenticationResultResource = MutableLiveData<Resource<Boolean>>()

    /**
     *  Checks if we are authenticated.
     */
    fun isAuthenticated(): Boolean = userService.isAuthenticated()

    /**
     *  Returns the user id of the currently authenticated user, or
     *  null if we don't have an authenticated user.
     */
    fun getSelfIdOrNull(): UUID? = userService.getUserIdOrNull()

    /**
     *  Gets the cached user account name if we have one.
     */
    fun getCachedEmailOrNull(): String? = userService.getCachedEmailOrNull()

    // TODO Optimize firebase token get operation. Is this the way to go? Maybe it is though...
    /**
     *  Logs the user in. Note that this first gets the firebase
     *  token, then performs the login call.
     *
     *  @param email User login email.
     *  @param password User password.
     *  @param successCallback Optional success callback
     */
    fun login(email: String, password: String, successCallback: (() -> Unit)? = null) =
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
                                    userService.login(
                                        email = email,
                                        tokenWrapper = it
                                    )

                                    // Save the token wrapper locally to notify observers.
                                    authenticationResultResource.setSuccess(true)

                                    // If we have a success callback, invoke it.
                                    successCallback?.invoke()
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
     *  @param profileImageFile If assigned, we will also update the profile image.
     */
    fun register(registration: RegistrationItem, profileImageFile: File?) =
        viewModelScope.launch {
            compositeDisposable.add(authUseCase
                .register(registration.mapToDomain())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        // If we reach this point we have created the user. Call the
                        // login functionality right away so we can log the user in.
                        login(registration.email, registration.password) {
                            // The success callback: If we have a profile image file, try to update it.
                            profileImageFile?.let { file ->
                                updateProfileImageAfterRegistration(file)
                            }
                        }
                    },
                    {
                        authenticationResultResource.setError(it.message)
                        Log.e(TAG, "Could not register- ${it.message}")
                    }
                )
            )
        }

    // TODO This is medium ugly.
    /**
     *  Called as registration -> login -> success callback to set the profile image.
     *  The reason for this structure is that we don't have a profile image update
     *  uri when we're not logged in. This was easier than creating a separate API
     *  call for this.
     */
    private fun updateProfileImageAfterRegistration(profileImageFile: File) = viewModelScope.launch {
        // First get the current user complete (to get the upload uri).
        authUserUseCase.getSelf(true)
            .subscribeOn(Schedulers.io())
            .subscribe({ self ->

                // Extract the updatable properties
                val updateUser = self.mapToPresentation().extractUpdatableProperties()
                updateUser.profileImageFile = profileImageFile

                // Perform the actual update
                authUserUseCase.updateSelf(updateUser.mapToDomain(), self.profileImageUploadUri)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            Log.d(TAG, "Profile image updated successfully after registration")
                        },
                        {
                            Log.e(TAG, "Could not update profile image after registration")
                        }
                    )
            },
                {
                    Log.e(TAG, "Could not get just registered user - ${it.message}")
                }
            )
    }

    /**
     *  Logs the user out. Note that this will also disable any
     *  future notifications through firebase.
     */
    fun logout(context: Context) {
        // First cancel locally.
        cancelBackgroundWork(context)

        // Then execute remote.
        viewModelScope.launch {
            compositeDisposable.add(authUseCase
                .logout()
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        // Then logout locally
                        userService.logout()
                    },
                    {
                        // Then logout locally
                        userService.logout()
                        Log.e(TAG, "Could not logout properly - ${it.message}")
                    }
                )
            )
        }
    }

    /**
     *  Deletes the user account and logs the user out.
     */
    fun deleteAccount(context: Context) {
        // First cancel locally
        cancelBackgroundWork(context)

        // Then perform remote
        viewModelScope.launch {
            compositeDisposable.add(authUseCase
                .deleteAccount()
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        // Then logout locally
                        userService.logout()
                    },
                    {
                        // Then logout locally
                        userService.logout()
                        Log.e(TAG, "Could not delete account - ${it.message}")
                    }
                )
            )
        }
    }

    /**
     *  Cancel any background work. These jobs expect us to be logged in.
     */
    private fun cancelBackgroundWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(ReactionUploadWorker.WORK_TAG)
        WorkManager.getInstance(context).cancelAllWorkByTag(VlogUploadWorker.WORK_TAG)
    }


    companion object {
        private val TAG = AuthViewModel::class.java.simpleName
    }
}
