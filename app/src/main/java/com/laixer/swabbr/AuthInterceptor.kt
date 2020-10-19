package com.laixer.swabbr

import com.laixer.swabbr.presentation.auth.UserManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val userManager: UserManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = with(chain) {
            val request = chain.request()
            val newBuilder = request.newBuilder()

            if (!request.header("No-Authentication").isNullOrEmpty()) {
                chain.proceed(newBuilder.build())
            } else {
                userManager.token?.let {
                    chain.proceed(newBuilder.addHeader("Authorization", "Bearer $it").build())
                } ?: chain.proceed(newBuilder.build())
            }
        }

        if (response.code() == 401) {
            userManager.invalidate()
        }

        return response
    }
}
