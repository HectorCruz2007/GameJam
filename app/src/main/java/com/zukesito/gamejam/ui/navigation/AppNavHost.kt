package com.zukesito.gamejam.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zukesito.gamejam.R
import com.zukesito.gamejam.ui.auth.login.LoginScreen
import com.zukesito.gamejam.ui.auth.register.RegisterScreen
import com.zukesito.gamejam.ui.games.detail.GameDetailScreen
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
            // Feedback left by the detail or form screen before returning
            val resultMessageRes by entry.savedStateHandle
                .getStateFlow<Int?>(KEY_USER_MESSAGE, null)
                .collectAsStateWithLifecycle()

            GameListScreen(
                resultMessageRes = resultMessageRes,
                onResultMessageShown = { entry.savedStateHandle[KEY_USER_MESSAGE] = null },
                onGameClick = { gameId -> navController.navigate(GameDetailRoute(gameId)) },
                onLoggedOut = { navController.navigateToLogin() }
            )
        }

        composable<GameDetailRoute> {
            GameDetailScreen(
                onBack = { navController.popBackStack() },
                onDeleted = { navController.popBackWithMessage(R.string.message_game_deleted) },
                onLoggedOut = { navController.navigateToLogin() }
            )
        }
    }
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
