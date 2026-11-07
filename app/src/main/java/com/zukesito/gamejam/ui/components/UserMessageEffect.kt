package com.zukesito.gamejam.ui.components

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

// Shows a one time message in a snackbar
// The message is marked as shown first, so it never appears twice
@Composable
fun UserMessageEffect(
    @StringRes messageRes: Int?,
    snackbarHostState: SnackbarHostState,
    onShown: () -> Unit
) {
    val context = LocalContext.current
    val currentOnShown by rememberUpdatedState(onShown)
    // Screen scope, clearing the message must not cancel the visible snackbar
    val scope = rememberCoroutineScope()

    LaunchedEffect(messageRes) {
        if (messageRes != null) {
            val message = context.getString(messageRes)
            currentOnShown()
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    }
}
