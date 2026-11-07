package com.zukesito.gamejam.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zukesito.gamejam.R
import com.zukesito.gamejam.ui.auth.login.LoginScreen
import com.zukesito.gamejam.ui.auth.register.RegisterScreen
import com.zukesito.gamejam.ui.games.detail.GameDetailScreen
import com.zukesito.gamejam.ui.games.form.GameFormScreen
import com.zukesito.gamejam.ui.games.list.GameListScreen

// User clicks are wrapped in whenResumed so a double tap cannot navigate twice
@Composable
fun AppNavHost(
    startDestination: Any,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable<LoginRoute> { entry ->
            LoginScreen(
                onLoginSuccess = { navController.navigateToHome() },
                onNavigateToRegister = entry.whenResumed { navController.navigate(RegisterRoute) }
            )
        }

        composable<RegisterRoute> { entry ->
            RegisterScreen(
                onRegisterSuccess = { navController.navigateToHome() },
                onNavigateToLogin = entry.whenResumed { navController.popBackStack() }
            )
        }

        composable<GameListRoute> { entry ->
            val resultMessageRes by entry.collectResultMessage()

            GameListScreen(
                resultMessageRes = resultMessageRes,
                onResultMessageShown = { entry.clearResultMessage() },
                onGameClick = { gameId ->
                    if (entry.isResumed()) navController.navigate(GameDetailRoute(gameId))
                },
                onAddClick = entry.whenResumed { navController.navigate(GameFormRoute()) },
                onLoggedOut = { navController.navigateToLogin(sessionExpired = false) },
                onSessionExpired = { navController.navigateToLogin(sessionExpired = true) }
            )
        }

        composable<GameDetailRoute> { entry ->
            val resultMessageRes by entry.collectResultMessage()

            GameDetailScreen(
                resultMessageRes = resultMessageRes,
                onResultMessageShown = { entry.clearResultMessage() },
                onBack = entry.whenResumed { navController.popBackStack() },
                onEditClick = { gameId ->
                    if (entry.isResumed()) navController.navigate(GameFormRoute(gameId))
                },
                onDeleted = { navController.popBackWithMessage(R.string.message_game_deleted) },
                onSessionExpired = { navController.navigateToLogin(sessionExpired = true) }
            )
        }

        composable<GameFormRoute> { entry ->
            GameFormScreen(
                onBack = entry.whenResumed { navController.popBackStack() },
                onSaved = { messageRes -> navController.popBackWithMessage(messageRes) },
                onSessionExpired = { navController.navigateToLogin(sessionExpired = true) }
            )
        }
    }
}

// A screen that is leaving or covered is no longer resumed
private fun NavBackStackEntry.isResumed(): Boolean =
    lifecycle.currentState == Lifecycle.State.RESUMED

// Ignores the click when the screen is not resumed
private fun NavBackStackEntry.whenResumed(action: () -> Unit): () -> Unit = {
    if (isResumed()) action()
}

// Feedback left by the next screen before returning to this one
@Composable
private fun NavBackStackEntry.collectResultMessage(): State<Int?> =
    savedStateHandle.getStateFlow<Int?>(KEY_USER_MESSAGE, null).collectAsStateWithLifecycle()

private fun NavBackStackEntry.clearResultMessage() {
    savedStateHandle[KEY_USER_MESSAGE] = null
}

// Clears the whole back stack so Back cannot return to auth screens
private fun NavHostController.navigateToHome() {
    navigate(GameListRoute) {
        popUpTo(graph.id) { inclusive = true }
    }
}

// Clears the whole back stack so Back cannot return to Home after logout
private fun NavHostController.navigateToLogin(sessionExpired: Boolean) {
    navigate(LoginRoute(sessionExpired)) {
        popUpTo(graph.id) { inclusive = true }
    }
}

// Leaves a message for the previous screen and goes back to it
private fun NavHostController.popBackWithMessage(@StringRes messageRes: Int) {
    previousBackStackEntry?.savedStateHandle?.set(KEY_USER_MESSAGE, messageRes)
    popBackStack()
}
