package com.laixer.swabbr.services.okhttp

import com.laixer.swabbr.services.users.UserService
import okhttp3.Interceptor
import okhttp3.Response

/**
 *  Interceptor for API calls which handles our authorization header.
 *  Token refreshes are managed by the [UserService]. If no valid
 *  token can be obtained, the expired one is used.
 */
class AuthInterceptor(private val userService: UserService) : Interceptor {
    /**
     *  If our request needs authentication this method will add just that.
     *  Any present token will be added. If the token is expired, we notify
     *  our [UserService] of said fact.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        // Store our original request for potential later use.
        val originalRequest = chain.request()

        // Just continue if we don't require authentication.
        // Note that this is a custom header we add in the repo.
        if (originalRequest.header(HEADER_NO_AUTHENTICATION) == "true") {
            return chain.proceed(originalRequest.newBuilder().build())
        }

        // Try to get a valid token. Note that this interceptor does not
        // control any refresh operations. Just use the token we have.
        val token = userService.getTokenOrNull()
        val isTokenValid = userService.hasValidToken() // Triggers a refresh if required. TODO Ugly.

        // No token means we can't do anything. Just cancel without calling the API.
        if (token == null) {
            chain.call().cancel()
            return chain.proceed(originalRequest)
        }

        // Attach the token, even if it's expired.
        return chain.proceed(
            originalRequest
                .newBuilder()
                .removeHeader(HEADER_AUTHORIZATION)
                .addHeader(HEADER_AUTHORIZATION, "Bearer $token")
                .build()
        )
    }

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_NO_AUTHENTICATION = "No-Authentication"
    }
}
