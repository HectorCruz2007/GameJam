package com.zukesito.gamejam.data.remote.interceptor

import com.zukesito.gamejam.data.local.SessionStorage
import okhttp3.Interceptor
import okhttp3.Response

// Adds the Bearer token to every request when a session exists
class AuthInterceptor(
    private val sessionStorage: SessionStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionStorage.getToken()
        val request = if (token == null) {
            chain.request()
        } else {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }
        return chain.proceed(request)
    }
}
