package com.zukesito.gamejam

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.zukesito.gamejam.ui.navigation.AppNavHost
import com.zukesito.gamejam.ui.navigation.GameListRoute
import com.zukesito.gamejam.ui.navigation.LoginRoute
import com.zukesito.gamejam.ui.theme.GameJamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Light icons on the system bars because the theme is always dark
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        // An active session skips the login screen
        val authRepository = (application as GameJamApplication).container.authRepository
        val startDestination: Any = if (authRepository.isLoggedIn()) GameListRoute else LoginRoute()

        setContent {
            GameJamTheme {
                AppNavHost(startDestination = startDestination)
            }
        }
    }
}
