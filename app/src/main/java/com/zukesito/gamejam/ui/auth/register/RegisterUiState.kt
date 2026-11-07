package com.zukesito.gamejam.ui.auth.register

import androidx.annotation.StringRes

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    @StringRes val nameError: Int? = null,
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val confirmPasswordError: Int? = null,
    @StringRes val requestError: Int? = null,
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false
)
