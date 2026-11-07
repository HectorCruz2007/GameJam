package com.zukesito.gamejam.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
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

@Composable
fun AppNavHost(
    startDestination: Any,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = { navController.navigateToHome() },
                onNavigateToRegister = { navController.navigate(RegisterRoute) }
            )
        }

        composable<RegisterRoute> {
            RegisterScreen(
                onRegisterSuccess = { navController.navigateToHome() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable<GameListRoute> { entry ->
            val resultMessageRes by entry.collectResultMessage()

            GameListScreen(
                resultMessageRes = resultMessageRes,
                onResultMessageShown = { entry.clearResultMessage() },
                onGameClick = { gameId -> navController.navigate(GameDetailRoute(gameId)) },
                onAddClick = { navController.navigate(GameFormRoute()) },
                onLoggedOut = { navController.navigateToLogin() }
            )
        }

        composable<GameDetailRoute> { entry ->
            val resultMessageRes by entry.collectResultMessage()

            GameDetailScreen(
                resultMessageRes = resultMessageRes,
                onResultMessageShown = { entry.clearResultMessage() },
                onBack = { navController.popBackStack() },
                onEditClick = { gameId -> navController.navigate(GameFormRoute(gameId)) },
                onDeleted = { navController.popBackWithMessage(R.string.message_game_deleted) },
                onLoggedOut = { navController.navigateToLogin() }
            )
        }

        composable<GameFormRoute> {
            GameFormScreen(
                onBack = { navController.popBackStack() },
                onSaved = { messageRes -> navController.popBackWithMessage(messageRes) },
                onLoggedOut = { navController.navigateToLogin() }
            )
        }
    }
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
private fun NavHostController.navigateToLogin() {
    navigate(LoginRoute) {
        popUpTo(graph.id) { inclusive = true }
    }
}

// Leaves a message for the previous screen and goes back to it
private fun NavHostController.popBackWithMessage(@StringRes messageRes: Int) {
    previousBackStackEntry?.savedStateHandle?.set(KEY_USER_MESSAGE, messageRes)
    popBackStack()
}
