package com.zukesito.gamejam.ui.auth.login

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
import com.zukesito.gamejam.ui.auth.validateRequired
import com.zukesito.gamejam.ui.common.appContainer
import com.zukesito.gamejam.ui.common.toMessageRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, requestError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, requestError = null) }
    }

    fun onLoginClick() {
        val state = _uiState.value
        if (state.isLoading) return

        val emailError = validateEmail(state.email)
        val passwordError = validateRequired(state.password)
        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, requestError = null) }
            authRepository.login(state.email.trim(), state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, requestError = error.toLoginMessage()) }
                }
        }
    }

    // On login a 401 means wrong credentials, not an expired session
    @StringRes
    private fun Throwable.toLoginMessage(): Int =
        if (this is AppException.Unauthorized) R.string.error_invalid_credentials else toMessageRes()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { LoginViewModel(appContainer().authRepository) }
        }
    }
}
