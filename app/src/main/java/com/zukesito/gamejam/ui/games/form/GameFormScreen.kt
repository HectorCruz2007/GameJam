package com.zukesito.gamejam.ui.games.form

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.zukesito.gamejam.ui.common.toDisplayText
import com.zukesito.gamejam.ui.components.FormTextField
import com.zukesito.gamejam.ui.components.FullScreenError
import com.zukesito.gamejam.ui.components.FullScreenLoading
import com.zukesito.gamejam.ui.components.LoadingButton
import com.zukesito.gamejam.ui.components.UserMessageEffect
import com.zukesito.gamejam.ui.theme.GameJamTheme

// Reusable form for create and edit, onSaved receives the feedback message
@Composable
fun GameFormScreen(
    onBack: () -> Unit,
    onSaved: (messageRes: Int) -> Unit,
    onSessionExpired: () -> Unit,
    viewModel: GameFormViewModel = viewModel(factory = GameFormViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.savedMessageRes) {
        uiState.savedMessageRes?.let(onSaved)
    }
    LaunchedEffect(uiState.isSessionExpired) {
        if (uiState.isSessionExpired) onSessionExpired()
    }
    UserMessageEffect(
        messageRes = uiState.userMessageRes,
        snackbarHostState = snackbarHostState,
        onShown = viewModel::onUserMessageShown
    )

    GameFormContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        actions = GameFormActions(
            onBack = onBack,
            onRetry = viewModel::loadGame,
            onTitleChange = viewModel::onTitleChange,
            onPlatformChange = viewModel::onPlatformChange,
            onHoursPlayedChange = viewModel::onHoursPlayedChange,
            onRatingChange = viewModel::onRatingChange,
            onCompletedChange = viewModel::onCompletedChange,
            onDatePickerOpen = viewModel::onDatePickerOpen,
            onSaveClick = viewModel::onSaveClick
        )
    )

    if (uiState.isDatePickerVisible) {
        StartDatePickerDialog(
            initialDate = uiState.startDate,
            onDateSelected = viewModel::onStartDateSelected,
            onDismiss = viewModel::onDatePickerDismiss
        )
    }
}

// Groups the form events to keep the content signature short
data class GameFormActions(
    val onBack: () -> Unit = {},
    val onRetry: () -> Unit = {},
    val onTitleChange: (String) -> Unit = {},
    val onPlatformChange: (String) -> Unit = {},
    val onHoursPlayedChange: (String) -> Unit = {},
    val onRatingChange: (String) -> Unit = {},
    val onCompletedChange: (Boolean) -> Unit = {},
    val onDatePickerOpen: () -> Unit = {},
    val onSaveClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameFormContent(
    uiState: GameFormUiState,
    snackbarHostState: SnackbarHostState,
    actions: GameFormActions
) {
    @StringRes val titleRes = if (uiState.isEditing) R.string.title_edit_game else R.string.title_new_game

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(titleRes)) },
                navigationIcon = {
                    TextButton(onClick = actions.onBack) {
                        Text(stringResource(R.string.action_back))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> FullScreenLoading()
                uiState.loadErrorRes != null ->
                    FullScreenError(messageRes = uiState.loadErrorRes, onRetry = actions.onRetry)
                else -> GameFormFields(uiState = uiState, actions = actions)
            }
        }
    }
}

@Composable
private fun GameFormFields(uiState: GameFormUiState, actions: GameFormActions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FormTextField(
            value = uiState.title,
            onValueChange = actions.onTitleChange,
            label = stringResource(R.string.label_title),
            errorRes = uiState.titleError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            )
        )
        FormTextField(
            value = uiState.platform,
            onValueChange = actions.onPlatformChange,
            label = stringResource(R.string.label_platform),
            errorRes = uiState.platformError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            )
        )
        FormTextField(
            value = uiState.hoursPlayed,
            onValueChange = actions.onHoursPlayedChange,
            label = stringResource(R.string.label_hours_played),
            errorRes = uiState.hoursPlayedError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )
        FormTextField(
            value = uiState.rating,
            onValueChange = actions.onRatingChange,
            label = stringResource(R.string.label_rating),
            errorRes = uiState.ratingError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )
        FormTextField(
            value = uiState.startDate.toDisplayText(),
            onValueChange = {},
            label = stringResource(R.string.label_start_date),
            errorRes = uiState.startDateError,
            readOnly = true,
            trailingIcon = {
                TextButton(onClick = actions.onDatePickerOpen) {
                    Text(stringResource(R.string.action_choose))
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.label_completed),
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(checked = uiState.completed, onCheckedChange = actions.onCompletedChange)
        }
        LoadingButton(
            text = stringResource(R.string.action_save),
            isLoading = uiState.isSaving,
            onClick = actions.onSaveClick,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview
@Composable
private fun GameFormContentPreview() {
    GameJamTheme {
        GameFormContent(
            uiState = GameFormUiState(title = "Celeste", platform = "Switch", hoursPlayed = "12", rating = "9"),
            snackbarHostState = remember { SnackbarHostState() },
            actions = GameFormActions()
        )
    }
}
