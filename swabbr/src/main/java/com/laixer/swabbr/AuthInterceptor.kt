package com.laixer.swabbr

import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authCacheDataSource: AuthCacheDataSource
) : Interceptor {

    var cookie: String = ""

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = authCacheDataSource.getToken().blockingGet()

        with(chain.request()) {
            if (!header("No-Authentication").isNullOrEmpty() ||
                token.isNullOrEmpty()
            ) {
                var response = chain.proceed(
                    newBuilder()
                        .build()
                )
                cookie = response.headers("Set-Cookie")[0].toString()
                return response
            }

            return chain.proceed(
                newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .addHeader("Cookie", cookie)
                    .build()
            )
        }
    }
}
