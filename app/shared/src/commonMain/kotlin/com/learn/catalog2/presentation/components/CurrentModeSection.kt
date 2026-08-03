package com.learn.catalog2.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CurrentModeSection(
    isSeniorMode: Boolean,
    onModeChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Current Mode",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = if (isSeniorMode)
                    "Publishing & managing guides"
                else
                    "Browsing & downloading guides",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                FilterChip(
                    selected = !isSeniorMode,
                    onClick = { onModeChange(false) },
                    label = { Text("Junior") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = isSeniorMode,
                    onClick = { onModeChange(true) },
                    label = { Text("Senior") }
                )
            }
        }
    }
}