package com.learn.catalog2.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.*
import com.learn.catalog2.domain.models.DataModels.Course
import com.learn.catalog2.presentation.viewmodels.ExploreViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OfflineScreen(
    viewModel: ExploreViewModel = koinViewModel()
) {
    // جلب الكتالوجات المحملة فقط من الـ Repository
    val downloadedGuides by viewModel.trendingCourses.collectAsState() 
    // ملاحظة: في النسخة النهائية سنضيف Flow مخصص لـ getDownloadedGuides() في ViewModel

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = "Your Offline Library",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Access your downloaded guides anytime, anywhere.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        if (downloadedGuides.none { it.isDownloaded }) {
            EmptyOfflineState()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(downloadedGuides.filter { it.isDownloaded }, key = { it.id }) { guide ->
                    OfflineGuideCard(guide = guide)
                }
            }
        }
    }
}

@Composable
private fun OfflineGuideCard(guide: Course) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFF3FAE5A).copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.outline_description_24),
                contentDescription = null,
                tint = Color(0xFF3FAE5A)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(guide.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Available Offline", fontSize = 12.sp, color = Color(0xFF3FAE5A))
        }

        Button(
            onClick = { /* فتح الملف باستخدام FileSystem لكل منصة */ },
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Text("Open")
        }
    }
}

@Composable
private fun EmptyOfflineState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.outline_download_2_24),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(16.dp))
        Text("No downloads yet", fontWeight = FontWeight.Medium)
        Text(
            "Guides you download will appear here.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
