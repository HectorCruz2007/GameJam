package com.zukesito.gamejam.ui.games.form

import androidx.annotation.StringRes
import java.time.LocalDate

// Numbers are kept as text so the user can type freely
data class GameFormUiState(
    val title: String = "",
    val platform: String = "",
    val hoursPlayed: String = "",
    val rating: String = "",
    val startDate: LocalDate = LocalDate.now(),
    val completed: Boolean = false,
    @StringRes val titleError: Int? = null,
    @StringRes val platformError: Int? = null,
    @StringRes val hoursPlayedError: Int? = null,
    @StringRes val ratingError: Int? = null,
    @StringRes val startDateError: Int? = null,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    @StringRes val loadErrorRes: Int? = null,
    val isDatePickerVisible: Boolean = false,
    val isSaving: Boolean = false,
    @StringRes val savedMessageRes: Int? = null,
    @StringRes val userMessageRes: Int? = null,
    val isLoggedOut: Boolean = false
) {
    val hasErrors: Boolean
        get() = listOf(titleError, platformError, hoursPlayedError, ratingError, startDateError)
            .any { it != null }
}
