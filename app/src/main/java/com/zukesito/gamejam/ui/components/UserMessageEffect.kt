package com.zukesito.gamejam.ui.components

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

// Shows a one time message in a snackbar and reports when it was shown
@Composable
fun UserMessageEffect(
    @StringRes messageRes: Int?,
    snackbarHostState: SnackbarHostState,
    onShown: () -> Unit
) {
    val context = LocalContext.current
    val currentOnShown by rememberUpdatedState(onShown)

    LaunchedEffect(messageRes) {
        if (messageRes != null) {
            snackbarHostState.showSnackbar(context.getString(messageRes))
            currentOnShown()
        }
    }
}
