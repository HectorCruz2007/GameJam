package com.zukesito.gamejam.domain.repository

import com.zukesito.gamejam.domain.model.User

// Authentication contract, implementation stores the token
interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    fun isLoggedIn(): Boolean
    fun logout()
}
