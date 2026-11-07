package com.zukesito.gamejam.ui.games.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.zukesito.gamejam.domain.error.AppException
import com.zukesito.gamejam.domain.repository.GameRepository
import com.zukesito.gamejam.ui.common.appContainer
import com.zukesito.gamejam.ui.common.toMessageRes
import com.zukesito.gamejam.ui.navigation.GameDetailRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository
) : ViewModel() {

    // The id comes from the navigation route
    private val gameId: Int = savedStateHandle.toRoute<GameDetailRoute>().gameId

    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    // Called every time the screen is shown, so edits are reflected
    fun loadGame() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorRes = null) }
            gameRepository.getById(gameId)
                .onSuccess { game ->
                    _uiState.update { it.copy(game = game, isLoading = false) }
                }
                .onFailure { error -> handleFailure(error, isLoadError = true) }
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(isDeleteDialogVisible = true) }
    }

    fun onDeleteDismiss() {
        _uiState.update { it.copy(isDeleteDialogVisible = false) }
    }

    fun onDeleteConfirm() {
        if (_uiState.value.isDeleting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleteDialogVisible = false, isDeleting = true) }
            gameRepository.delete(gameId)
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false, isDeleted = true) }
                }
                .onFailure { error -> handleFailure(error, isLoadError = false) }
        }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessageRes = null) }
    }

    // Load errors replace the screen, delete errors go to a snackbar
    private fun handleFailure(error: Throwable, isLoadError: Boolean) {
        _uiState.update {
            when {
                error is AppException.Unauthorized ->
                    it.copy(isLoading = false, isDeleting = false, isLoggedOut = true)
                isLoadError && it.game == null ->
                    it.copy(isLoading = false, errorRes = error.toMessageRes())
                else ->
                    it.copy(isLoading = false, isDeleting = false, userMessageRes = error.toMessageRes())
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                GameDetailViewModel(createSavedStateHandle(), appContainer().gameRepository)
            }
        }
    }
}
