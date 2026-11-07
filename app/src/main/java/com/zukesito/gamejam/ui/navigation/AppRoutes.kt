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

// Id used by the form to create a new game instead of editing one
const val NEW_GAME_ID = 0

@Serializable
data class GameFormRoute(val gameId: Int = NEW_GAME_ID)
