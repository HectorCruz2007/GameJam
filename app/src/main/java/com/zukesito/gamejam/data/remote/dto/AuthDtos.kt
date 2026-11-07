package com.zukesito.gamejam.data.remote.dto

import kotlinx.serialization.Serializable

// Transport objects for the auth endpoints

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val email: String
)

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val user: UserDto
)
