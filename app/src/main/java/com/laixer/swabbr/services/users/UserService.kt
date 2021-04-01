package com.laixer.swabbr.services.users

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import com.laixer.swabbr.domain.model.TokenWrapper
import com.laixer.swabbr.presentation.utils.todosortme.setSuccess
import com.laixer.swabbr.utils.cache.Cache
import com.laixer.swabbr.utils.resources.Resource
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

// TODO Should this be a background service? Maybe yes?
// TODO This shouldn't care about email.
/**
 *  Manages our tokens and user login state. Register this as a singleton for this
 *  to work as intended.
 *
 *  This class acts as a replacement for Android's AccountManager. We have chosen
 *  not to implement said class due to lacking documentation and not being able to
 *  get said class working as intended. Using it only to store tokens seemed too
 *  redundant.
 *  Corresponding issue: https://github.com/Laixer/Swabbr-Android/issues/173
 */
class UserService(cache: Cache) : TokenService(cache) {

    /**
     *  If this is set to true, we can't authenticate the user in any way with the
     *  data stored in this class. This means both the auth token and refresh token
     *  have expired or are invalid. This resource can be observed.
     */
    var newAuthenticationRequiredResource = MutableLiveData<Resource<Boolean>>()

    /**
     *  State enum. That will start by checking if
     *  we have a user or not.
     */
    private lateinit var state: UserServiceState

    /**
     *  Set the initial state based on what we have.
     */
    init {
        val hasToken = getTokenOrNull() != null
        val hasValidRefreshToken = hasValidRefreshToken()

        state = if (hasToken && hasValidRefreshToken) {
            // If we have any token and have a valid refresh token we
            // can maintain authentication. Set the flag to logged in.
            UserServiceState.LOGGED_IN
        } else {
            // If we reach this point we should re-login.
            UserServiceState.NO_USER
        }
    }

    /**
     *  Checks if we are authenticated or not.
     */
    fun isAuthenticated() =
        getTokenOrNull() != null &&
            hasValidRefreshToken()

    // TODO https://github.com/pilgr/Paper/issues/4
    /**
     *  Checks if we have a valid token available. If we should have
     *  one but don't, this will trigger a refresh operation.
     */
    fun hasValidToken(tokenBufferInSeconds: Long = TOKEN_BUFFER_SECONDS_REQUIRES_REFRESH): Boolean = try {
        cache.get<JWT>(KEY_ACCOUNT_TOKEN)?.let { token ->
            // TODO Clean
            // We don't use jwt.isExpired() here since this method has a leeway parameter
            // which also applies to the issue date. This caused troubles for our check.
            // Checking it manually like this will always act as required.
            val dateExpiredFromToken = token.expiresAt?.toInstant() ?: return false
            val expiresZonedDateTime = ZonedDateTime.ofInstant(dateExpiredFromToken, ZoneId.of("UTC"))

            // Apply buffer.
            val expirationMoment = expiresZonedDateTime.minusSeconds(tokenBufferInSeconds)

            val now = ZonedDateTime.now(ZoneId.of("UTC"))

            if (expirationMoment > now) {
                return true
            } else {

                // If we have no valid token this will trigger a refresh operation if required.
                if (state == UserServiceState.LOGGED_IN) {
                    tryRefreshToken()
                }
            }
        }

        false
    } catch (e: Exception) {
        Log.e(TAG, "Couldn't check for valid token, returning false")
        false
    }


    /**
     *  Stores who we are and stores the provided tokens.
     *
     *  @param email User email.
     *  @param tokenWrapper User token wrapper response.
     */
    fun login(email: String, tokenWrapper: TokenWrapper) {
        if (state != UserServiceState.NO_USER) {
            Log.w(TAG, "Illegal state change from $state")
            return
        }

        cache.set(KEY_ACCOUNT_EMAIL, email)

        setNewTokenWrapper(tokenWrapper)

        // We are no longer unable to authenticate.
        newAuthenticationRequiredResource.setSuccess(false)

        // Do the state change last.
        state = UserServiceState.LOGGED_IN
    }

    /**
     *  Logs the current user out.
     */
    fun logout() {
        if (state != UserServiceState.LOGGED_IN &&
            state != UserServiceState.LOGGED_IN_REFRESHING
        ) {
            Log.w(TAG, "Illegal state change from $state")
            return
        }

        cache.remove(KEY_ACCOUNT_EMAIL)

        clearTokenCacheCompletely()

        // Do the state change before dispatch
        state = UserServiceState.NO_USER

        newAuthenticationRequiredResource.setSuccess(true)
    }

    // TODO https://github.com/pilgr/Paper/issues/4
    /**
     *  Checks if we have a valid refresh token available.
     */
    private fun hasValidRefreshToken(): Boolean = try {
        cache.get<String>(KEY_ACCOUNT_REFRESH_TOKEN)?.let { refreshToken ->
            cache.get<JWT>(KEY_ACCOUNT_TOKEN)?.let { token ->
                // TODO Clean
                val dateStartFromToken = token.notBefore?.toInstant() ?: return false
                val zdtStart = ZonedDateTime.ofInstant(dateStartFromToken, ZoneId.of("UTC"))
                val exp = cache.get<Int>(KEY_ACCOUNT_REFRESH_TOKEN_EXPIRATION_MINUTES)
                val expDate =
                    zdtStart?.plusMinutes(exp?.toLong() ?: 0)?.minusSeconds(UserService.REFRESH_TOKEN_BUFFER_SECONDS)

                if (expDate != null && expDate > ZonedDateTime.now()) {
                    return true
                }
            }
        }

        false
    } catch (e: Exception) {
        Log.e(TAG, "Couldn't check if we have a valid refresh token, returning false")
        false
    }

    /**
     *  Attempts to refresh the current authentication token.
     */
    private fun tryRefreshToken() {
        if (state != UserServiceState.LOGGED_IN) {
            Log.w(TAG, "Illegal state change from $state")
            return
        }

        // Do the state change before doing anything else to prevent race conditions.
        state = UserServiceState.LOGGED_IN_REFRESHING

        // If we don't have a valid refresh token we can't do anything
        if (!hasValidRefreshToken()) {
            onTokenRefreshFailed()
            return
        }

        /**
         *  Dispatch the actual token get operation, which in turn will
         *  call [onTokenRefreshSucceeded] or [onTokenRefreshFailed].
         */
        refreshToken()
    }

    /**
     *  Should be called when token refresh has succeeded.
     */
    override fun onTokenRefreshSucceeded(tokenWrapper: TokenWrapper) {
        if (state != UserServiceState.LOGGED_IN_REFRESHING) {
            Log.w(TAG, "Illegal state change from $state")
            return
        }

        super.onTokenRefreshSucceeded(tokenWrapper)

        // Do the state change last
        state = UserServiceState.LOGGED_IN
    }

    /**
     *  Should be called when token refresh has failed.
     */
    override fun onTokenRefreshFailed() {
        if (state != UserServiceState.LOGGED_IN_REFRESHING) {
            Log.w(TAG, "Illegal state change from $state")
            return
        }

        // Do the state change before dispatching the resource update.
        state = UserServiceState.NO_USER

        // We can't do anything at this point, notify any observers.
        newAuthenticationRequiredResource.setSuccess(true)
    }

    /**
     *  Gets the cached user account name if we have one.
     */
    fun getCachedEmailOrNull(): String? = cache.get<String>(KEY_ACCOUNT_EMAIL)

    /**
     *  Gets the cached user id if we have one. Note that whenever
     *  we are logged in this should return. If we can guarantee
     *  that we are logged in, call [getUserId] instead.
     */
    fun getUserIdOrNull(): UUID? = cache.get<UUID>(KEY_ACCOUNT_USER_ID)

    /**
     *  Gets the cached user id. Only call this if we can guarantee
     *  that we are currently logged in.
     */
    fun getUserId(): UUID = cache.get<UUID>(KEY_ACCOUNT_USER_ID)!!

    companion object {
        private val TAG = UserService::class.java.simpleName

        /**
         *  Used to determine if we require a token refresh or not.
         */
        private const val TOKEN_BUFFER_SECONDS_REQUIRES_REFRESH = 24 * 60L // One hour

        private const val REFRESH_TOKEN_BUFFER_SECONDS = 24 * 60 * 60L // One day

        const val KEY_ACCOUNT_EMAIL = "account_email"
    }
}
