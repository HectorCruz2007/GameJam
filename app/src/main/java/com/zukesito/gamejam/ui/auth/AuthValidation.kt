package com.zukesito.gamejam.ui.auth

import androidx.annotation.StringRes
import com.zukesito.gamejam.R

// Validation rules shared by login and register, null means valid

private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private const val MIN_PASSWORD_LENGTH = 8
private const val MAX_NAME_LENGTH = 60

@StringRes
fun validateName(name: String): Int? = when {
    name.isBlank() -> R.string.error_required
    name.trim().length > MAX_NAME_LENGTH -> R.string.error_name_length
    else -> null
}

@StringRes
fun validateEmail(email: String): Int? = when {
    email.isBlank() -> R.string.error_required
    !EMAIL_REGEX.matches(email.trim()) -> R.string.error_invalid_email
    else -> null
}

@StringRes
fun validateRequired(value: String): Int? =
    if (value.isBlank()) R.string.error_required else null

@StringRes
fun validateNewPassword(password: String): Int? = when {
    password.isBlank() -> R.string.error_required
    password.length < MIN_PASSWORD_LENGTH -> R.string.error_password_length
    else -> null
}

@StringRes
fun validatePasswordConfirmation(password: String, confirmation: String): Int? = when {
    confirmation.isBlank() -> R.string.error_required
    password != confirmation -> R.string.error_password_mismatch
    else -> null
}
