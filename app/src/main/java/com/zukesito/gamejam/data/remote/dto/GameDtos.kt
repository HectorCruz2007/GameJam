package com.zukesito.gamejam.data.remote.dto

import kotlinx.serialization.Serializable

// Transport objects for the games endpoints, dates use yyyy-MM-dd

@Serializable
data class GameDto(
    val id: Int,
    val title: String,
    val platform: String,
    val hoursPlayed: Int,
    val rating: Int,
    val startDate: String,
    val completed: Boolean
)

// Body for create and update, the id goes in the path
@Serializable
data class GameRequestDto(
    val title: String,
    val platform: String,
    val hoursPlayed: Int,
    val rating: Int,
    val startDate: String,
    val completed: Boolean
)
