package com.laixer.swabbr

import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authCacheDataSource: AuthCacheDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = authCacheDataSource.getToken().blockingGet()

        val request = chain.request()
        val builder = request.newBuilder()

        // Check if request requires authenticaton
        if (request.header("No-Authentication").isNullOrEmpty()) {
            // Authentication required
            if (!token.isNullOrEmpty()) {
                builder.addHeader("Authorization", "Bearer $token")
            }
        }

        return chain.proceed(request)
    }
}
