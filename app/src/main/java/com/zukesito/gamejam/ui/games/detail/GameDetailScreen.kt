package com.zukesito.gamejam.ui.games.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zukesito.gamejam.R
import com.zukesito.gamejam.domain.model.Game
import com.zukesito.gamejam.ui.common.toDisplayText
import com.zukesito.gamejam.ui.components.FullScreenError
import com.zukesito.gamejam.ui.components.FullScreenLoading
import com.zukesito.gamejam.ui.components.UserMessageEffect
import com.zukesito.gamejam.ui.games.list.CompletionStatusText
import com.zukesito.gamejam.ui.theme.GameJamTheme
import java.time.LocalDate

// Shows every attribute of one game and allows editing or deleting it
@Composable
fun GameDetailScreen(
    @StringRes resultMessageRes: Int?,
    onResultMessageShown: () -> Unit,
    onBack: () -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleted: () -> Unit,
    onSessionExpired: () -> Unit,
    viewModel: GameDetailViewModel = viewModel(factory = GameDetailViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.loadGame() }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) onDeleted()
    }
    LaunchedEffect(uiState.isSessionExpired) {
        if (uiState.isSessionExpired) onSessionExpired()
    }
    UserMessageEffect(
        messageRes = resultMessageRes,
        snackbarHostState = snackbarHostState,
        onShown = onResultMessageShown
    )
    UserMessageEffect(
        messageRes = uiState.userMessageRes,
        snackbarHostState = snackbarHostState,
        onShown = viewModel::onUserMessageShown
    )

    GameDetailContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onEditClick = onEditClick,
        onRetry = viewModel::loadGame,
        onDeleteClick = viewModel::onDeleteClick,
        onDeleteConfirm = viewModel::onDeleteConfirm,
        onDeleteDismiss = viewModel::onDeleteDismiss
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameDetailContent(
    uiState: GameDetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onEditClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onDeleteClick: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteDismiss: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_game_detail)) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
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
                uiState.game != null -> GameDetailBody(
                    game = uiState.game,
                    isDeleting = uiState.isDeleting,
                    onEditClick = { onEditClick(uiState.game.id) },
                    onDeleteClick = onDeleteClick
                )
                uiState.errorRes != null -> FullScreenError(messageRes = uiState.errorRes, onRetry = onRetry)
                else -> FullScreenLoading()
            }
        }
    }

    if (uiState.isDeleteDialogVisible && uiState.game != null) {
        DeleteGameDialog(
            gameTitle = uiState.game.title,
            onConfirm = onDeleteConfirm,
            onDismiss = onDeleteDismiss
        )
    }
}

@Composable
private fun GameDetailBody(
    game: Game,
    isDeleting: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = game.title, style = MaterialTheme.typography.headlineMedium)
        HorizontalDivider()

        DetailRow(label = stringResource(R.string.label_platform), value = game.platform)
        DetailRow(label = stringResource(R.string.label_hours_played), value = game.hoursPlayed.toString())
        DetailRow(
            label = stringResource(R.string.label_rating),
            value = stringResource(R.string.format_rating, game.rating)
        )
        DetailRow(label = stringResource(R.string.label_start_date), value = game.startDate.toDisplayText())
        DetailRow(label = stringResource(R.string.label_status)) {
            CompletionStatusText(completed = game.completed)
        }

        OutlinedButton(
            onClick = onEditClick,
            enabled = !isDeleting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.action_edit))
        }
        Button(
            onClick = onDeleteClick,
            enabled = !isDeleting,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isDeleting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.action_delete))
            }
        }
    }
}

@Preview
@Composable
private fun GameDetailContentPreview() {
    GameJamTheme {
        GameDetailContent(
            uiState = GameDetailUiState(
                game = Game(1, "Hollow Knight", "PC", 42, 10, LocalDate.of(2026, 9, 1), true),
                isLoading = false
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onEditClick = {},
            onRetry = {},
            onDeleteClick = {},
            onDeleteConfirm = {},
            onDeleteDismiss = {}
        )
    }
}
