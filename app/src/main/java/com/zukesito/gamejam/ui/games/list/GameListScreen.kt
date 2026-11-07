package com.zukesito.gamejam.ui.games.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.zukesito.gamejam.ui.components.FullScreenError
import com.zukesito.gamejam.ui.components.FullScreenLoading
import com.zukesito.gamejam.ui.components.FullScreenMessage
import com.zukesito.gamejam.ui.theme.GameJamTheme
import java.time.LocalDate

// Home screen, lists the games of the user
@Composable
fun GameListScreen(
    onGameClick: (Int) -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: GameListViewModel = viewModel(factory = GameListViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Reload when the screen comes back, for example after create or edit
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.loadGames() }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) onLoggedOut()
    }

    GameListContent(
        uiState = uiState,
        onGameClick = onGameClick,
        onRetry = viewModel::loadGames,
        onLogoutClick = viewModel::onLogoutClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameListContent(
    uiState: GameListUiState,
    onGameClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_game_list)) },
                actions = {
                    TextButton(onClick = onLogoutClick) {
                        Text(stringResource(R.string.action_logout))
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // Full spinner only on the first load, later reloads keep the list visible
                uiState.isLoading && uiState.games.isEmpty() -> FullScreenLoading()
                uiState.errorRes != null && uiState.games.isEmpty() ->
                    FullScreenError(messageRes = uiState.errorRes, onRetry = onRetry)
                uiState.isEmpty -> FullScreenMessage(messageRes = R.string.empty_game_list)
                else -> GameList(games = uiState.games, onGameClick = onGameClick)
            }
            if (uiState.isLoading && uiState.games.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun GameList(games: List<Game>, onGameClick: (Int) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = games, key = { it.id }) { game ->
            GameListItem(game = game, onClick = { onGameClick(game.id) })
        }
    }
}

@Preview
@Composable
private fun GameListContentPreview() {
    GameJamTheme {
        GameListContent(
            uiState = GameListUiState(
                games = listOf(
                    Game(1, "Hollow Knight", "PC", 42, 10, LocalDate.of(2026, 9, 1), true),
                    Game(2, "Celeste", "Switch", 12, 9, LocalDate.of(2026, 10, 15), false)
                ),
                isLoading = false
            ),
            onGameClick = {},
            onRetry = {},
            onLogoutClick = {}
        )
    }
}
