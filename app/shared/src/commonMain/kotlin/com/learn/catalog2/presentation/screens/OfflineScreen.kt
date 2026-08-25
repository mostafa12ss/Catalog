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
import catalog2.app.shared.generated.resources.available_offline
import catalog2.app.shared.generated.resources.no_downloads_sub
import catalog2.app.shared.generated.resources.no_downloads_title
import catalog2.app.shared.generated.resources.offline_library_sub
import catalog2.app.shared.generated.resources.offline_library_title
import catalog2.app.shared.generated.resources.open_button
import catalog2.app.shared.generated.resources.outline_description_24
import catalog2.app.shared.generated.resources.outline_download_2_24
import com.learn.catalog2.domain.models.DataModels.Course
import com.learn.catalog2.presentation.Navigation.LocalBottomPadding
import com.learn.catalog2.presentation.utils.FileOpener
import com.learn.catalog2.presentation.viewmodels.ExploreViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OfflineScreen(
    viewModel: ExploreViewModel = koinViewModel()
) {
    val downloadedGuides by viewModel.trendingCourses.collectAsState()

    // 🟢 جلب قيمة الـ Padding السفلي الخاصة بالبار الطافي من الـ CompositionLocal
    val bottomPadding = LocalBottomPadding.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // Title & Subtitle / العنوان والشرح
        Text(
            text = stringResource(Res.string.offline_library_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(Res.string.offline_library_sub),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        val offlineItems = downloadedGuides.filter { it.isDownloaded }

        if (offlineItems.isEmpty()) {
            EmptyOfflineState(bottomPadding = bottomPadding)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    top = 4.dp,
                    bottom = bottomPadding + 24.dp // 🟢 رفع آخر عنصر فوق الشريط العائم بوضوح
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(offlineItems, key = { it.id }) { guide ->
                    OfflineGuideCard(guide = guide)
                }
            }
        }
    }
}

@Composable
private fun OfflineGuideCard(guide: Course) {
    val fileOpener = remember { FileOpener() }

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
            Text(
                text = stringResource(Res.string.available_offline),
                fontSize = 12.sp,
                color = Color(0xFF3FAE5A)
            )
        }

        Button(
            onClick = {
                val pathToOpen = guide.localPath ?: guide.fileUrls.firstOrNull() ?: ""

                println("🔍 File Path to open: $pathToOpen")
                if (pathToOpen.isNotEmpty()) {
                    fileOpener.openFile(pathToOpen)
                }
            },
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Text(stringResource(Res.string.open_button))
        }
    }
}

@Composable
private fun EmptyOfflineState(bottomPadding: androidx.compose.ui.unit.Dp) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomPadding), // 🟢 تعويض البار السفلي لمنع انزياح الأيقونة لأسفل
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
        Text(
            text = stringResource(Res.string.no_downloads_title),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = stringResource(Res.string.no_downloads_sub),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}