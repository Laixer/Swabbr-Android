package com.laixer.swabbr.services.okhttp

import com.laixer.swabbr.BuildConfig
import com.laixer.swabbr.data.model.TokenWrapperEntity
import com.laixer.swabbr.data.model.mapToDomain
import com.laixer.swabbr.services.moshi.buildWithCustomAdapters
import com.laixer.swabbr.services.users.UserManager
import com.squareup.moshi.Moshi
import okhttp3.*
import org.json.JSONObject

/**
 *  Interceptor for API calls which handles our authorization header.
 *  This also performs token refreshes if required. The design of this
 *  class was based on:
 *  https://gist.github.com/joanb/14bfe27f82642fcca82c453e2c4d7ffe
 */
class AuthInterceptor(private val userManager: UserManager) : Interceptor {
    /**
     *  If our request needs authentication this method will add just that.
     *  If no valid token is present but we do have a valid refresh token, this
     *  will perform a token refresh operation. After that a token is added to
     *  the request if one is present.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        // Store our original request for potential later use.
        val originalRequest = chain.request()

        // Just continue if we don't require authentication.
        // Note that this is a custom header we add in the repo.
        if (originalRequest.header("No-Authentication") == "true") {
            return chain.proceedInvalidatingTokenOnError(originalRequest.newBuilder().build())
        }

        // If we have the resources to refresh, do so. We perform
        // this operation before trying to add a token to the request.
        tryRefreshTokenIfRequired(chain, originalRequest)

        // If we have a valid token, add it in the authorization header and proceed.
        return if (userManager.hasValidToken()) {
            chain.proceedInvalidatingTokenOnError(
                originalRequest
                    .newBuilder()
                    .removeHeader(HEADER_AUTHORIZATION)
                    .addHeader(HEADER_AUTHORIZATION, "Bearer ${userManager.getTokenOrNull()}")
                    .build()
            )
        } else {
            // If we reach this point we can't get a valid token. Calling
            // invalidateCompletely() will make sure the frontend handles this.
            userManager.invalidateCompletely()

            // Proceeding the chain after calling cancel() makes sure that okhttp
            // cancels the original http request. The API will not be called.
            chain.call().cancel()
            return chain.proceed(originalRequest)
        }
    }

    // TODO Look into synchronization. Can this create a deadlock? Will okhttp time this out for us?
    // TODO Store isRefreshing?
    /**
     *  Synchronized attempt to get a refresh token. This should
     *  always execute only once concurrently, else we create a
     *  huge race condition.
     */
    @Synchronized
    private fun tryRefreshTokenIfRequired(chain: Interceptor.Chain, originalRequest: Request) {
        if (userManager.hasToken() && !userManager.hasValidToken() && userManager.hasValidRefreshToken()) {
            val jsonObject = JSONObject().run {
                put("refreshToken", userManager.getValidRefreshTokenOrNull() ?: "")
                put("expiredToken", userManager.getTokenOrNull()?.toString() ?: "")
            }
            val requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonObject.toString())
            val refreshRequest = originalRequest.newBuilder()
                .url(REFRESH_TOKEN_URL)
                .post(requestBody)
                .build()

            /** Don't call [proceedInvalidatingTokenOnError] here, handle errors in the else case later. */
            val refreshResponse = chain.proceed(refreshRequest)

            if (refreshResponse.isSuccessful) {
                val body = refreshResponse.body()
                val moshi = Moshi.Builder().buildWithCustomAdapters()
                val tokenWrapperAdapter = moshi.adapter(TokenWrapperEntity::class.java)

                tokenWrapperAdapter.fromJson(body!!.source())?.let {
                    userManager.setNewTokenWrapper(it.mapToDomain())
                }

                // According to the OkHttp3 docs we must close our response body.
                body.close()
            } else {
                // If we reach this, the token and/or refresh token turned out to be
                // invalid when we didn't expect this. This acts as a last resort.

                // TODO Note that we can reach if we login somewhere else. The refresh
                //  token will then be overwritten in the db. We don't support multiple
                //  logins at this moment, but neither should they happen. This will be
                //  a problem somewhere in the future, an issue was made at
                //  https://github.com/Laixer/Swabbr-Android/issues/174

                userManager.invalidateCompletely()
            }
        }
    }

    /**
     *  Execute a request and invalidate the access token if we get a 401 response.
     */
    private fun Interceptor.Chain.proceedInvalidatingTokenOnError(request: Request): Response {
        val response = proceed(request)

        // If we get a 401, something went wrong. All cases where we send
        // an invalid token to the backend should be covered already. This
        // will invalidate the session completely, we can't recover.
        if (response.code() == 401) {
            userManager.invalidateCompletely()
        }

        return response
    }

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val BASE_URL = BuildConfig.API_ENDPOINT

        // TODO We might want to add this to some configuration as well
        private const val REFRESH_TOKEN_URL = BASE_URL + "authentication/refresh-token"
        private val MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8")
    }
}
