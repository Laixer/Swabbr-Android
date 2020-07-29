package com.laixer.swabbr

import com.auth0.android.jwt.JWT
import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import okhttp3.Interceptor
import okhttp3.MediaType
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

            try {
                token = JWT(authCacheDataSource.get().blockingGet().jwtToken)
            } catch (e: Exception) {
                // If we land here the user is not authenticated but somehow tried to make a request that does require auth. Just return the chain with an error and handle it on fragment level
//                val errorResponseBody = ResponseBody.create(MediaType.get())
            }

            return chain.proceed(
                newBuilder().addHeader("Authorization", "Bearer $token").build()
            )
        }
    }
}
