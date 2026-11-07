package com.zukesito.gamejam.ui.games.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zukesito.gamejam.domain.error.AppException
import com.zukesito.gamejam.domain.repository.AuthRepository
import com.zukesito.gamejam.domain.repository.GameRepository
import com.zukesito.gamejam.ui.common.appContainer
import com.zukesito.gamejam.ui.common.toMessageRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameListViewModel(
    private val authRepository: AuthRepository,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameListUiState())
    val uiState: StateFlow<GameListUiState> = _uiState.asStateFlow()

    // Called every time the screen is shown, so the list is always fresh
    fun loadGames() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorRes = null) }
            gameRepository.getAll()
                .onSuccess { games ->
                    _uiState.update { it.copy(games = games, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        when {
                            // The repository already cleared the session on 401
                            error is AppException.Unauthorized ->
                                it.copy(isLoading = false, isLoggedOut = true)
                            // Keep the visible list and report the error in a snackbar
                            it.games.isNotEmpty() ->
                                it.copy(isLoading = false, userMessageRes = error.toMessageRes())
                            else ->
                                it.copy(isLoading = false, errorRes = error.toMessageRes())
                        }
                    }
                }
        }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessageRes = null) }
    }

    fun onLogoutClick() {
        authRepository.logout()
        _uiState.update { it.copy(isLoggedOut = true) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                GameListViewModel(container.authRepository, container.gameRepository)
            }
        }
    }
}
