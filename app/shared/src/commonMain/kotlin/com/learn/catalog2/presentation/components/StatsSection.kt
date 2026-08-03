package com.learn.catalog2.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learn.catalog2.domain.models.AppUser

@Composable
fun StatsSection(user: AppUser, isSeniorMode: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!isSeniorMode) {
            StatCard(title = "3", subtitle = "Guides Owned")
            StatCard(title = "265", subtitle = "Pts Spent")
            StatCard(title = "3", subtitle = "Offline")
        } else {
            StatCard(title = "2", subtitle = "Published")
            StatCard(title = "2,755", subtitle = "Downloads")
            StatCard(title = "26.9K", subtitle = "Earned (pts)")
        }
    }
}

@Composable
fun RowScope.StatCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
