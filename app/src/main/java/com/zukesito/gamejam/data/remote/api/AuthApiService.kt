package com.zukesito.gamejam.data.remote.api

import com.zukesito.gamejam.data.remote.dto.AuthResponseDto
import com.zukesito.gamejam.data.remote.dto.LoginRequestDto
import com.zukesito.gamejam.data.remote.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("register")
    suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto

    @POST("login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto
}
