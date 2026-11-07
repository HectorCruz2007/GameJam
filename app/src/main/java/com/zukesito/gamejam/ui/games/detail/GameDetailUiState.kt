package com.zukesito.gamejam.ui.games.detail

import androidx.annotation.StringRes
import com.zukesito.gamejam.domain.model.Game

data class GameDetailUiState(
    val game: Game? = null,
    val isLoading: Boolean = true,
    @StringRes val errorRes: Int? = null,
    val isDeleteDialogVisible: Boolean = false,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false,
    @StringRes val userMessageRes: Int? = null,
    val isLoggedOut: Boolean = false
)
