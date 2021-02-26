package com.laixer.swabbr.services.users

import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import com.laixer.cache.Cache
import com.laixer.presentation.Resource
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.model.TokenWrapper
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

// TODO Should this contain email?
// TODO Invalidate should maybe also clear the refresh token? We should have
//  the ability to force a login by the user if refreshing also doesn't work.
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
class UserManager(private val cache: Cache) {
    /**
     *  Resource indicating when the [invalidateCompletely] method has been called.
     *  If this is set to true, we can't authenticate the user in any way with the
     *  data stored in this class. This means both the auth token and refresh token
     *  have expired or are invalid.
     */
    var newAuthenticationRequiredResource = MutableLiveData<Resource<Boolean>>()

    /**
     *  Flag used to determine if [invalidateToken] was called or not.
     */
    private var isInvalidated = false

    /**
     *  Checks if we have any token available.
     */
    fun hasToken() : Boolean = cache.get<JWT>(KEY_ACCOUNT_TOKEN) != null

    /**
     *  Checks if we have a valid token available. If the
     *  [isInvalidated] flag is set to true this returns false.
     */
    fun hasValidToken(): Boolean {
        if (isInvalidated) {
            return false
        }

        cache.get<JWT>(KEY_ACCOUNT_TOKEN)?.let {
            // TODO Clean
            // We don't use jwt.isExpired() here since. This method has a leeway parameter
            // which also applies to the issue date. This caused troubles for our check.
            // Checking it manually like this will always act as required.
            val dateExpiredFromToken = getTokenOrNull()?.expiresAt?.toInstant()
            val ztdExpires = if (dateExpiredFromToken != null) ZonedDateTime.ofInstant(dateExpiredFromToken, ZoneId.of("UTC")) else null
            val expDate = ztdExpires?.minusSeconds(TOKEN_BUFFER_SECONDS)

            if (expDate != null && expDate > ZonedDateTime.now()) {
                return true
            }
        }

        return false
    }

    /**
     *  Checks if we have a valid refresh token available.
     *
     *  @param bufferInSeconds Buffer to our validation time interval.
     */
    fun hasValidRefreshToken(bufferInSeconds: Long = REFRESH_TOKEN_BUFFER_SECONDS): Boolean {
        cache.get<String>(KEY_ACCOUNT_REFRESH_TOKEN)?.let {
            // TODO Clean
            val dateStartFromToken = getTokenOrNull()?.notBefore?.toInstant()
            val zdtStart = if (dateStartFromToken != null) ZonedDateTime.ofInstant(dateStartFromToken, ZoneId.of("UTC")) else null
            val exp = cache.get<Int>(KEY_ACCOUNT_REFRESH_TOKEN_EXPIRATION_MINUTES)
            val expDate = zdtStart?.plusMinutes(exp?.toLong() ?: 0)?.minusSeconds(bufferInSeconds)

            if (expDate != null && expDate > ZonedDateTime.now()) {
                return true
            }
        }

        return false
    }

    /**
     *  Gets the current authentication token if a valid one is available.
     *  This will return null if [isInvalidated] is set to true.
     */
    fun getValidTokenOrNull(): JWT? {
        if (isInvalidated) {
            return null
        }

        cache.get<JWT>(KEY_ACCOUNT_TOKEN)?.let {
            return if (it.isExpired(TOKEN_BUFFER_SECONDS)) it else null
        }

        return null
    }

    /**
     *  Gets the stored token if we have one, even if it's expired.
     *  If we don't have a token stored this returns null.
     */
    fun getTokenOrNull(): JWT? = cache.get<JWT>(KEY_ACCOUNT_TOKEN)

    /**
     *  Gets the stored refresh token if we have one that's valid. If
     *  we don't have a refresh token stored this returns null.
     */
    fun getValidRefreshTokenOrNull(): String? {
        cache.get<String>(KEY_ACCOUNT_REFRESH_TOKEN)?.let {
            return if (hasValidRefreshToken()) it else null
        }

        return null
    }

    /**
     *  Logs the current user out.
     */
    fun logout() {
        cache.remove(KEY_ACCOUNT_TOKEN)
        cache.remove(KEY_ACCOUNT_REFRESH_TOKEN)

        // TODO Like this?
        // Trigger the logout resource
        invalidateCompletely()
    }

    /**
     *  Invalidates the current user's session. Call this when
     *  a token that seemed valid turns out to be invalid. If
     *  we get a 401 response from the API this should be called.
     */
    fun invalidateToken() { isInvalidated = true }

    /**
     *  Call this if refreshing didn't work, but we expected it to work.
     *  This will invalidate and clear everything so a new login can be
     *  triggered when reading the availability of tokens in this class.
     */
    fun invalidateCompletely() {
        invalidateToken()

        // TODO Is this our way? Maybe not.
        cache.remove(KEY_ACCOUNT_REFRESH_TOKEN)

        // Notify all listeners we are unable to authenticate.
        newAuthenticationRequiredResource.setSuccess(true)
    }

    /**
     *  Stores who we are and stores the provided tokens.
     *
     *  @param email User email.
     *  @param tokenWrapper User token wrapper response.
     */
    fun login(email: String, tokenWrapper: TokenWrapper) {
        if (tokenWrapper.token.isExpired(0)) {
            throw IllegalArgumentException("Token is already expired")
        }

        // TODO Just store a wrapper which is modifiable?
        cache.set(KEY_ACCOUNT_USER_ID, tokenWrapper.userId)
        cache.set(KEY_ACCOUNT_EMAIL, email)
        cache.set(KEY_ACCOUNT_TOKEN, tokenWrapper.token)
        cache.set(KEY_ACCOUNT_REFRESH_TOKEN, tokenWrapper.refreshToken)
        cache.set(KEY_ACCOUNT_TOKEN_EXPIRATION_MINUTES, tokenWrapper.tokenExpirationInMinutes)
        cache.set(KEY_ACCOUNT_REFRESH_TOKEN_EXPIRATION_MINUTES, tokenWrapper.refreshTokenExpirationInMinutes)

        isInvalidated = false
        // We are no longer unable to authenticate.
        newAuthenticationRequiredResource.setSuccess(false)
    }

    /**
     *  Call this after a token refresh operation has been completed. This
     *  will override all stored token values - simulating a "new" login.
     */
    fun setNewTokenWrapper(tokenWrapper: TokenWrapper) {
        // TODO Validate identity

        login(getCachedEmailOrNull()!!, tokenWrapper)
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
        const val TAG = "UserManager"

        const val KEY_ACCOUNT_USER_ID = "account_user_id"
        const val KEY_ACCOUNT_EMAIL = "account_email"
        const val KEY_ACCOUNT_TOKEN = "account_token"
        const val KEY_ACCOUNT_TOKEN_DATE_ISSUED = "account_token_date_issued"
        const val KEY_ACCOUNT_TOKEN_EXPIRATION_MINUTES = "account_token_date_expiration_minutes"
        const val KEY_ACCOUNT_REFRESH_TOKEN = "account_refresh_token"
        const val KEY_ACCOUNT_REFRESH_TOKEN_EXPIRATION_MINUTES = "account_refresh_token_expiration_minutes"

        const val TOKEN_BUFFER_SECONDS = 30L
        const val REFRESH_TOKEN_BUFFER_SECONDS = 30L
    }
}
