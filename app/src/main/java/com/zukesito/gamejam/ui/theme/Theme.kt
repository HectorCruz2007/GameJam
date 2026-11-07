package com.zukesito.gamejam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Single dark scheme, dynamic color is disabled to keep the Steam look
private val SteamColorScheme = darkColorScheme(
    primary = SteamBlue,
    onPrimary = SteamNavyDark,
    primaryContainer = SteamSlate,
    onPrimaryContainer = SteamText,
    secondary = SteamSteel,
    onSecondary = SteamText,
    secondaryContainer = SteamSlate,
    onSecondaryContainer = SteamText,
    tertiary = SteamGreen,
    onTertiary = SteamNavyDark,
    background = SteamNavy,
    onBackground = SteamText,
    surface = SteamNavy,
    onSurface = SteamText,
    surfaceVariant = SteamSlate,
    onSurfaceVariant = SteamTextMuted,
    surfaceContainerLowest = SteamNavyDark,
    surfaceContainerLow = SteamNavy,
    surfaceContainer = SteamNavyLight,
    surfaceContainerHigh = SteamNavyLight,
    surfaceContainerHighest = SteamSlate,
    outline = SteamTextMuted,
    outlineVariant = SteamSlate,
    error = SteamRed,
    onError = SteamNavyDark
)

@Composable
fun GameJamTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SteamColorScheme,
        typography = Typography,
        content = content
    )
}
