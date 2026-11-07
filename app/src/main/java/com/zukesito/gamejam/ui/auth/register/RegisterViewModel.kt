package com.zukesito.gamejam.ui.auth.register

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zukesito.gamejam.R
import com.zukesito.gamejam.domain.error.AppException
import com.zukesito.gamejam.domain.repository.AuthRepository
import com.zukesito.gamejam.ui.auth.validateEmail
import com.zukesito.gamejam.ui.auth.validateName
import com.zukesito.gamejam.ui.auth.validateNewPassword
import com.zukesito.gamejam.ui.auth.validatePasswordConfirmation
import com.zukesito.gamejam.ui.common.appContainer
import com.zukesito.gamejam.ui.common.toMessageRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null, requestError = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, requestError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, requestError = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, requestError = null)
        }
    }

    fun onRegisterClick() {
        val state = _uiState.value
        if (state.isLoading) return

        val validated = state.copy(
            nameError = validateName(state.name),
            emailError = validateEmail(state.email),
            passwordError = validateNewPassword(state.password),
            confirmPasswordError = validatePasswordConfirmation(state.password, state.confirmPassword)
        )
        if (validated.hasErrors()) {
            _uiState.value = validated
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, requestError = null) }
            authRepository.register(state.name.trim(), state.email.trim(), state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isRegistered = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, requestError = error.toRegisterMessage()) }
                }
        }
    }

    private fun RegisterUiState.hasErrors(): Boolean =
        listOf(nameError, emailError, passwordError, confirmPasswordError).any { it != null }

    // On register a 409 means the email is already used
    @StringRes
    private fun Throwable.toRegisterMessage(): Int =
        if (this is AppException.Conflict) R.string.error_email_taken else toMessageRes()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { RegisterViewModel(appContainer().authRepository) }
        }
    }
}
