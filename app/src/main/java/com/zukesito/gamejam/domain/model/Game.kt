package com.zukesito.gamejam.domain.model

import java.time.LocalDate

// Main domain resource, id 0 means not saved yet
data class Game(
    val id: Int = 0,
    val title: String,
    val platform: String,
    val hoursPlayed: Int,
    val rating: Int,
    val startDate: LocalDate,
    val completed: Boolean
)
