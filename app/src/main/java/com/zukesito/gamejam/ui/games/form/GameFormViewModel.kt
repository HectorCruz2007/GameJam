package com.zukesito.gamejam.ui.games.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.zukesito.gamejam.R
import com.zukesito.gamejam.domain.error.AppException
import com.zukesito.gamejam.domain.model.Game
import com.zukesito.gamejam.domain.repository.GameRepository
import com.zukesito.gamejam.ui.common.appContainer
import com.zukesito.gamejam.ui.common.toMessageRes
import com.zukesito.gamejam.ui.navigation.GameFormRoute
import com.zukesito.gamejam.ui.navigation.NEW_GAME_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

// Shared by create and edit, the route id decides the mode
class GameFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val gameId: Int = savedStateHandle.toRoute<GameFormRoute>().gameId
    private val isEditing: Boolean = gameId != NEW_GAME_ID

    private val _uiState = MutableStateFlow(GameFormUiState(isEditing = isEditing))
    val uiState: StateFlow<GameFormUiState> = _uiState.asStateFlow()

    init {
        // Loaded once, so typed changes survive rotation
        if (isEditing) loadGame()
    }

    fun loadGame() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadErrorRes = null) }
            gameRepository.getById(gameId)
                .onSuccess { game -> _uiState.update { it.withGame(game) } }
                .onFailure { error -> handleFailure(error, isLoadError = true) }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }

    fun onPlatformChange(platform: String) {
        _uiState.update { it.copy(platform = platform, platformError = null) }
    }

    // Only digits are accepted, the length avoids Int overflow
    fun onHoursPlayedChange(hoursPlayed: String) {
        _uiState.update {
            it.copy(hoursPlayed = hoursPlayed.filter(Char::isDigit).take(5), hoursPlayedError = null)
        }
    }

    fun onRatingChange(rating: String) {
        _uiState.update {
            it.copy(rating = rating.filter(Char::isDigit).take(2), ratingError = null)
        }
    }

    fun onCompletedChange(completed: Boolean) {
        _uiState.update { it.copy(completed = completed) }
    }

    fun onDatePickerOpen() {
        _uiState.update { it.copy(isDatePickerVisible = true) }
    }

    fun onDatePickerDismiss() {
        _uiState.update { it.copy(isDatePickerVisible = false) }
    }

    fun onStartDateSelected(startDate: LocalDate) {
        _uiState.update {
            it.copy(startDate = startDate, startDateError = null, isDatePickerVisible = false)
        }
    }

    fun onSaveClick() {
        val state = _uiState.value
        if (state.isSaving || state.isLoading) return

        val validated = state.copy(
            titleError = validateTitle(state.title),
            platformError = validatePlatform(state.platform),
            hoursPlayedError = validateHoursPlayed(state.hoursPlayed),
            ratingError = validateRating(state.rating),
            startDateError = validateStartDate(state.startDate)
        )
        if (validated.hasErrors) {
            _uiState.value = validated
            return
        }

        val game = state.toGame()
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = if (isEditing) gameRepository.update(game) else gameRepository.create(game)
            result
                .onSuccess {
                    val messageRes = if (isEditing) R.string.message_game_updated else R.string.message_game_created
                    _uiState.update { it.copy(isSaving = false, savedMessageRes = messageRes) }
                }
                .onFailure { error -> handleFailure(error, isLoadError = false) }
        }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessageRes = null) }
    }

    // Load errors replace the form, save errors go to a snackbar
    private fun handleFailure(error: Throwable, isLoadError: Boolean) {
        _uiState.update {
            when {
                error is AppException.Unauthorized ->
                    it.copy(isLoading = false, isSaving = false, isSessionExpired = true)
                isLoadError ->
                    it.copy(isLoading = false, loadErrorRes = error.toMessageRes())
                else ->
                    it.copy(isSaving = false, userMessageRes = error.toMessageRes())
            }
        }
    }

    private fun GameFormUiState.withGame(game: Game): GameFormUiState = copy(
        title = game.title,
        platform = game.platform,
        hoursPlayed = game.hoursPlayed.toString(),
        rating = game.rating.toString(),
        startDate = game.startDate,
        completed = game.completed,
        isLoading = false
    )

    // Called only after validation, so the numbers are safe to parse
    private fun GameFormUiState.toGame(): Game = Game(
        id = gameId,
        title = title.trim(),
        platform = platform.trim(),
        hoursPlayed = hoursPlayed.toInt(),
        rating = rating.toInt(),
        startDate = startDate,
        completed = completed
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                GameFormViewModel(createSavedStateHandle(), appContainer().gameRepository)
            }
        }
    }
}
