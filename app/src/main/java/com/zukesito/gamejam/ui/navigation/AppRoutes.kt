package com.zukesito.gamejam.ui.navigation

import kotlinx.serialization.Serializable

// Type safe destinations for Navigation Compose

@Serializable
data object LoginRoute

@Serializable
data object RegisterRoute

@Serializable
data object GameListRoute

@Serializable
data class GameDetailRoute(val gameId: Int)
