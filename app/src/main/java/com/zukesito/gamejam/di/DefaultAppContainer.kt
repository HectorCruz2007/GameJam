package com.zukesito.gamejam.di

import android.content.Context
import com.zukesito.gamejam.BuildConfig
import com.zukesito.gamejam.data.local.SessionStorage
import com.zukesito.gamejam.data.local.SharedPreferencesSessionStorage
import com.zukesito.gamejam.data.remote.api.AuthApiService
import com.zukesito.gamejam.data.remote.api.GameApiService
import com.zukesito.gamejam.data.remote.interceptor.AuthInterceptor
import com.zukesito.gamejam.data.repository.AuthRepositoryImpl
import com.zukesito.gamejam.data.repository.GameRepositoryImpl
import com.zukesito.gamejam.domain.repository.AuthRepository
import com.zukesito.gamejam.domain.repository.GameRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

// Builds every dependency once, created only by GameJamApplication
class DefaultAppContainer(context: Context) : AppContainer {

    private val sessionStorage: SessionStorage =
        SharedPreferencesSessionStorage(context.applicationContext)

    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(sessionStorage))
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                // Request logs only in debug builds
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(retrofit.create(AuthApiService::class.java), sessionStorage)
    }

    override val gameRepository: GameRepository by lazy {
        GameRepositoryImpl(retrofit.create(GameApiService::class.java), sessionStorage)
    }
}
