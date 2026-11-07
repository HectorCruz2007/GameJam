package com.zukesito.gamejam.ui.games.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Label above its value, used for each attribute of the game
@Composable
fun DetailRow(
    label: String,
    modifier: Modifier = Modifier,
    value: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        value()
    }
}

// Shortcut for plain text values
@Composable
fun DetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    DetailRow(label = label, modifier = modifier) {
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
