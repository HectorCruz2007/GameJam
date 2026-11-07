package com.zukesito.gamejam.ui.games.list

import androidx.annotation.StringRes
import com.zukesito.gamejam.domain.model.Game

data class GameListUiState(
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = true,
    @StringRes val errorRes: Int? = null,
    val isLoggedOut: Boolean = false
) {
    // Empty only after a successful load with no games
    val isEmpty: Boolean get() = !isLoading && errorRes == null && games.isEmpty()
}
