package com.zukesito.gamejam.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zukesito.gamejam.ui.auth.login.LoginScreen
import com.zukesito.gamejam.ui.auth.register.RegisterScreen
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

        composable<GameListRoute> {
            GameListScreen(
                onGameClick = { /* Detail screen arrives in step 6c */ },
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
