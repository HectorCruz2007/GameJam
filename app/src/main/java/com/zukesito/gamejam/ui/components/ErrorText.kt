package com.zukesito.gamejam.ui.components

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

// Request error shown below a form, renders nothing when null
@Composable
fun ErrorText(@StringRes messageRes: Int?, modifier: Modifier = Modifier) {
    if (messageRes == null) return
    Text(
        text = stringResource(messageRes),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
    )
}
