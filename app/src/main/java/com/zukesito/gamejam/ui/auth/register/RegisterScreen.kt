package com.zukesito.gamejam.ui.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zukesito.gamejam.R
import com.zukesito.gamejam.ui.components.ErrorText
import com.zukesito.gamejam.ui.components.FormTextField
import com.zukesito.gamejam.ui.components.LoadingButton
import com.zukesito.gamejam.ui.components.PasswordTextField
import com.zukesito.gamejam.ui.theme.GameJamTheme

// Connects the ViewModel with the stateless content
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) onRegisterSuccess()
    }

    RegisterContent(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onRegisterClick = viewModel::onRegisterClick,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
private fun RegisterContent(
    uiState: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.title_register), style = MaterialTheme.typography.headlineMedium)

            FormTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = stringResource(R.string.label_name),
                errorRes = uiState.nameError,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            FormTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.label_email),
                errorRes = uiState.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            PasswordTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.label_password),
                errorRes = uiState.passwordError,
                imeAction = ImeAction.Next
            )
            PasswordTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = stringResource(R.string.label_confirm_password),
                errorRes = uiState.confirmPasswordError
            )
            ErrorText(messageRes = uiState.requestError)
            LoadingButton(
                text = stringResource(R.string.action_register),
                isLoading = uiState.isLoading,
                onClick = onRegisterClick
            )
            TextButton(onClick = onNavigateToLogin, enabled = !uiState.isLoading) {
                Text(stringResource(R.string.action_go_to_login))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterContentPreview() {
    GameJamTheme {
        RegisterContent(RegisterUiState(), {}, {}, {}, {}, {}, {})
    }
}
