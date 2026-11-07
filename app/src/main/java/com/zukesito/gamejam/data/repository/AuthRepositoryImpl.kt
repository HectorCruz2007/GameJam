package com.zukesito.gamejam.data.repository

import com.zukesito.gamejam.data.local.SessionStorage
import com.zukesito.gamejam.data.mapper.toDomain
import com.zukesito.gamejam.data.remote.api.AuthApiService
import com.zukesito.gamejam.data.remote.dto.AuthResponseDto
import com.zukesito.gamejam.data.remote.dto.LoginRequestDto
import com.zukesito.gamejam.data.remote.dto.RegisterRequestDto
import com.zukesito.gamejam.data.remote.safeApiCall
import com.zukesito.gamejam.domain.model.User
import com.zukesito.gamejam.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val sessionStorage: SessionStorage
) : AuthRepository {

    override suspend fun register(name: String, email: String, password: String): Result<User> =
        safeApiCall {
            authApiService.register(RegisterRequestDto(name, email, password)).saveSession()
        }

    override suspend fun login(email: String, password: String): Result<User> =
        safeApiCall {
            authApiService.login(LoginRequestDto(email, password)).saveSession()
        }

    override fun isLoggedIn(): Boolean = sessionStorage.getToken() != null

    override fun logout() = sessionStorage.clear()

    // Keeps the token and returns only the domain user
    private fun AuthResponseDto.saveSession(): User {
        sessionStorage.saveToken(accessToken)
        return user.toDomain()
    }
}
