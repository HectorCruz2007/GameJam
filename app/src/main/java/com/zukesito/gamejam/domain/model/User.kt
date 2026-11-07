package com.zukesito.gamejam.domain.model

// Authenticated user, the token stays in the data layer
data class User(
    val id: Int,
    val name: String,
    val email: String
)
