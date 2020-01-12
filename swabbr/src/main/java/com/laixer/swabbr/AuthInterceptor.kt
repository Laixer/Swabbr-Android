package com.laixer.swabbr

import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authCacheDataSource: AuthCacheDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = authCacheDataSource.getToken().blockingGet()

        with(chain.request()) {
            if (!header("No-Authentication").isNullOrEmpty() ||
                token.isNullOrEmpty()
            ) {
                return chain.proceed(
                    newBuilder()
                        .build()
                )
            }

            return chain.proceed(
                newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            )
        }
    }
}
