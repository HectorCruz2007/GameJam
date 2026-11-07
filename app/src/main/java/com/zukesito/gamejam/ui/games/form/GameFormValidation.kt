package com.zukesito.gamejam.ui.games.form

import androidx.annotation.StringRes
import com.zukesito.gamejam.R
import java.time.LocalDate

// Validation rules for the game form, null means valid

private const val MAX_TITLE_LENGTH = 60
private const val MAX_PLATFORM_LENGTH = 40
private val RATING_RANGE = 1..10

@StringRes
fun validateTitle(title: String): Int? = when {
    title.isBlank() -> R.string.error_required
    title.trim().length > MAX_TITLE_LENGTH -> R.string.error_title_length
    else -> null
}

@StringRes
fun validatePlatform(platform: String): Int? = when {
    platform.isBlank() -> R.string.error_required
    platform.trim().length > MAX_PLATFORM_LENGTH -> R.string.error_platform_length
    else -> null
}

@StringRes
fun validateHoursPlayed(hoursPlayed: String): Int? = when {
    hoursPlayed.isBlank() -> R.string.error_required
    hoursPlayed.toIntOrNull() == null -> R.string.error_invalid_number
    else -> null
}

@StringRes
fun validateRating(rating: String): Int? {
    val value = rating.toIntOrNull()
    return when {
        rating.isBlank() -> R.string.error_required
        value == null -> R.string.error_invalid_number
        value !in RATING_RANGE -> R.string.error_rating_range
        else -> null
    }
}

@StringRes
fun validateStartDate(startDate: LocalDate): Int? =
    if (startDate.isAfter(LocalDate.now())) R.string.error_future_date else null
