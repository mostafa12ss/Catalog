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
import com.learn.catalog2.data.repository.UserProfileStats
import com.learn.catalog2.domain.models.AppUser

@Composable
fun StatsSection(
    user: AppUser,
    isSeniorMode: Boolean,
    stats: UserProfileStats = UserProfileStats()
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!isSeniorMode) {
            // Junior Mode
            StatCard(title = stats.guidesOwned.toString(), subtitle = "Guides Owned")
            StatCard(title = stats.ptsSpent.toString(), subtitle = "Pts Spent")
            StatCard(title = stats.offlineGuides.toString(), subtitle = "Offline")
        } else {
            // Senior Mode (ديناميكي الآن)
            StatCard(
                title = stats.publishedGuidesCount.toString(),
                subtitle = "Published"
            )
            StatCard(
                title = formatNumber(stats.totalDownloads),
                subtitle = "Downloads"
            )
            StatCard(
                title = formatNumber(stats.totalEarnedPoints),
                subtitle = "Earned (pts)"
            )
        }
    }
}

// دالة لتنسيق الأرقام الكبيرة (مثل 2.7K أو 26.9K)
private fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> {
            val value = (number / 100_000).toDouble() / 10.0
            "${value}M"
        }
        number >= 1_000 -> {
            val value = (number / 100).toDouble() / 10.0
            "${value}K"
        }
        else -> number.toString()
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