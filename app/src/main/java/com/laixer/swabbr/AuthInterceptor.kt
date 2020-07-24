package com.laixer.swabbr

import com.auth0.android.jwt.JWT
import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authCacheDataSource: AuthCacheDataSource
) : Interceptor {

    private var token: JWT? = null
    override fun intercept(chain: Interceptor.Chain): Response {
        with(chain.request()) {
            if (!header("No-Authentication").isNullOrEmpty()) {
                return chain.proceed(
                    newBuilder().build()
                )
            }

            if (token == null) {
                token = JWT(authCacheDataSource.get().blockingGet().jwtToken)
            }

            return chain.proceed(
                newBuilder().addHeader("Authorization", "Bearer $token").build()
            )
        }
    }
}
