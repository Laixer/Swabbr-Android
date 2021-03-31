package com.laixer.swabbr.services.users

import androidx.annotation.CallSuper
import com.auth0.android.jwt.JWT
import com.laixer.swabbr.BuildConfig
import com.laixer.swabbr.data.model.TokenWrapperEntity
import com.laixer.swabbr.data.model.mapToDomain
import com.laixer.swabbr.domain.model.TokenWrapper
import com.laixer.swabbr.services.moshi.buildWithCustomAdapters
import com.laixer.swabbr.utils.cache.Cache
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

/**
 *  Gets our tokens for us.
 */
abstract class TokenService(protected val cache: Cache) {

    /**
     *  Http client to refresh tokens.
     */
    private val httpClient by lazy {
        OkHttpClient
            .Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            }).build()
    }

    /**
     *  Gets the current jwt token.
     */
    fun getTokenOrNull(): JWT? = cache.get(KEY_ACCOUNT_TOKEN)

    /**
     *  Gets the current jwt token.
     */
    private fun getRefreshTokenOrNull(): String? = cache.get(KEY_ACCOUNT_REFRESH_TOKEN)

    /**
     *  Attempts to actually refresh the token. This dispatches to
     *  the IO thread.
     */
    protected fun refreshToken(
        successCallback: (tokenWrapper: TokenWrapper) -> Unit = ::onTokenRefreshSucceeded,
        failureCallback: () -> Unit = ::onTokenRefreshFailed
    ) =
        CoroutineScope(Dispatchers.IO).launch {
            // TODO This fails if we have no tokens ... which is fine?
            val jsonObject = JSONObject().run {
                put("refreshToken", getRefreshTokenOrNull() ?: "")
                put("expiredToken", getTokenOrNull() ?: "")
            }
            val requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonObject.toString())
            val refreshRequest = Request.Builder()
                .url(REFRESH_TOKEN_URL)
                .post(requestBody)
                .build()

            val refreshResponse = httpClient.newCall(refreshRequest).execute()

            if (refreshResponse.isSuccessful) {
                val body = refreshResponse.body()
                val moshi = Moshi.Builder().buildWithCustomAdapters()
                val tokenWrapperAdapter = moshi.adapter(TokenWrapperEntity::class.java)

                val tokenWrapper = tokenWrapperAdapter.fromJson(body!!.source())
                if (tokenWrapper != null) {
                    successCallback.invoke(tokenWrapper.mapToDomain())
//                    onTokenRefreshSucceeded(tokenWrapper.mapToDomain())
                }

                // According to the OkHttp3 docs we must close our response body.
                body.close()

                // If our token wrapper was empty, something went wrong. Call the fail callback.
                if (tokenWrapper == null) {
                    failureCallback.invoke()
//                    onTokenRefreshFailed()
                }

            } else {
                // If we reach this, the token and/or refresh token turned out to be
                // invalid when we didn't expect this. This acts as a last resort.

                // TODO Note that we can reach if we login somewhere else. The refresh
                //  token will then be overwritten in the db. We don't support multiple
                //  logins at this moment, but neither should they happen. This will be
                //  a problem somewhere in the future, an issue was made at
                //  https://github.com/Laixer/Swabbr-Android/issues/174

                failureCallback.invoke()
//                onTokenRefreshFailed()
            }
        }


    /**
     *  Override this to determine what happens if [refreshToken] succeeds.
     */
    @CallSuper
    protected open fun onTokenRefreshSucceeded(tokenWrapper: TokenWrapper) {
        setNewTokenWrapper(tokenWrapper)
    }

    /**
     *  Override this to determine what happens if [refreshToken] fails.
     */
    protected abstract fun onTokenRefreshFailed()

    /**
     *  Extracts and stores the token wrapper properties.
     */
    protected fun setNewTokenWrapper(tokenWrapper: TokenWrapper) {
        cache.set(KEY_ACCOUNT_USER_ID, tokenWrapper.userId)
        cache.set(KEY_ACCOUNT_TOKEN, tokenWrapper.token)
        cache.set(KEY_ACCOUNT_REFRESH_TOKEN, tokenWrapper.refreshToken)
        cache.set(KEY_ACCOUNT_TOKEN_EXPIRATION_MINUTES, tokenWrapper.tokenExpirationInMinutes)
        cache.set(KEY_ACCOUNT_REFRESH_TOKEN_EXPIRATION_MINUTES, tokenWrapper.refreshTokenExpirationInMinutes)
    }

    /**
     *  Clears all our token related properties from the cache.
     */
    protected fun clearTokenCacheCompletely() {
        cache.remove(KEY_ACCOUNT_USER_ID)
        cache.remove(KEY_ACCOUNT_REFRESH_TOKEN)
        cache.remove(KEY_ACCOUNT_REFRESH_TOKEN_EXPIRATION_MINUTES)
        cache.remove(KEY_ACCOUNT_TOKEN)
        cache.remove(KEY_ACCOUNT_TOKEN_EXPIRATION_MINUTES)
    }

    companion object {
        private val TAG = TokenService::class.java.simpleName

        const val KEY_ACCOUNT_USER_ID = "account_user_id"
        const val KEY_ACCOUNT_TOKEN = "account_token"
        const val KEY_ACCOUNT_TOKEN_EXPIRATION_MINUTES = "account_token_date_expiration_minutes"
        const val KEY_ACCOUNT_REFRESH_TOKEN = "account_refresh_token"
        const val KEY_ACCOUNT_REFRESH_TOKEN_EXPIRATION_MINUTES = "account_refresh_token_expiration_minutes"

        // TODO We might want to add this to some configuration as well
        private const val REFRESH_TOKEN_URL = BuildConfig.API_ENDPOINT + "authentication/refresh-token"
        private val MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8")
    }
}



