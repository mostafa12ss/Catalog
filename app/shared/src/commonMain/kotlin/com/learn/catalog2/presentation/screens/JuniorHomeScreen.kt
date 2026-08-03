package com.learn.catalog2.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.*
import com.learn.catalog2.domain.models.DataModels.Category
import com.learn.catalog2.domain.models.DataModels.Course
import com.learn.catalog2.presentation.viewmodels.ExploreViewModel

import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Preview(name = "Android Phone", device = "spec:width=360dp,height=800dp,dpi=440", showBackground = true)
@Preview(name = "iPhone 15 Pro", device = "spec:width=393dp,height=852dp,dpi=460", showBackground = true)
@Preview(name = "Desktop / Web", device = "spec:width=1280dp,height=800dp,dpi=160", showBackground = true)
annotation class MultiPlatformPreviews

@Composable
fun JuniorHomeScreen(
    viewModel: ExploreViewModel = koinViewModel()
) {
    val guides by viewModel.trendingCourses.collectAsState()
    val categories by viewModel.categories.collectAsState()

    JuniorHomeScreenContent(
        guides = guides,
        categories = categories,
        onSearch = { viewModel.searchGuides(it) },
        onFavoriteToggle = { id, isFav -> viewModel.toggleFavorite(id, isFav) }
    )
}

@Composable
fun JuniorHomeScreenContent(
    guides: List<Course>,
    categories: List<Category>,
    onSearch: (String) -> Unit,
    onFavoriteToggle: (String, Boolean) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("all") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearch(it)
                },
                placeholder = { Text(stringResource(Res.string.search_ph)) },
                leadingIcon = {
                    if (LocalInspectionMode.current) {
                        Text("🔍", fontSize = 18.sp)
                    } else {
                        Icon(
                            painter = painterResource(Res.drawable.baseline_search_24),
                            contentDescription = "Search"
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Tabs Categories
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    stringResource(Res.string.tab_all),
                    fontWeight = if (selectedCategoryId == "all") FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedCategoryId == "all") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable { selectedCategoryId = "all" }
                )
                categories.forEach { category ->
                    Text(
                        category.name,
                        fontWeight = if (selectedCategoryId == category.id) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedCategoryId == category.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { selectedCategoryId = category.id }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${guides.size} results",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
                OutlinedButton(onClick = { /* Filter Logic */ }, shape = RoundedCornerShape(50)) {
                    Icon(
                        painter = painterResource(Res.drawable.outline_filter_alt_24),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(Res.string.sort_filter), fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            // قائمة الكروت
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp), // المسافة بين الكروت
                contentPadding = PaddingValues(bottom = 20.dp),
                modifier = Modifier.fillMaxWidth() // 👈 شيلنا خلفية surfaceVariant عشان الفراغ الرمادي يختفي
            ) {
                items(guides, key = { it.id }) { guide ->
                    GuideListCard(
                        guide = guide,
                        onFavoriteToggle = { onFavoriteToggle(guide.id, !guide.isSaved) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GuideListCard(
    guide: Course,
    onFavoriteToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // 👈 إلغاء الظل السميِك
        border = BorderStroke(
            width = 0.8.dp, // 👈 خط رفيع جداً وأنيق بيقسم الكروت
            color = Color(0xFFE2E8F0)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFE0F2F1), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.outline_description_24),
                    contentDescription = null,
                    tint = Color(0xFF00796B)
                )
            }

            Spacer(Modifier.width(12.dp))

            // Info Section
            Column(modifier = Modifier.weight(1f)) {
                Text(guide.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(guide.author.ifEmpty { "Eng. Unknown" }, fontSize = 12.sp, color = Color.Gray)

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val levelColor = when (guide.level) {
                        "Advanced" -> Color(0xFFE05B5B)
                        "Intermediate" -> Color(0xFFEDA13B)
                        else -> Color(0xFF3FAE5A)
                    }
                    Box(
                        modifier = Modifier
                            .background(levelColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(guide.level, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = levelColor)
                    }

                    Text("${guide.downloads} dl", fontSize = 11.sp, color = Color.Gray)
                    Text("★ ${guide.rating}", fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.width(8.dp))

            // Action / Points Section
            Column(horizontalAlignment = Alignment.End) {
                Text("${guide.points} pts", color = Color(0xFFD97706), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))

                if (guide.isSaved) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✓ Saved", color = Color(0xFF00796B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = { /* Download */ },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(painter = painterResource(Res.drawable.outline_download_2_24), contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(Res.string.get_label), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// =======================================================================
// 🧪 Mock Data
// =======================================================================
private val mockCategories = listOf(
    // 👈 حذفنا عنصر "All Categories" المكرر لتجنب ظهور كلمة All مرتين
    Category(id = "1", name = "Robotics & Embedded", count = 5),
    Category(id = "2", name = "Android KMP", count = 4),
    Category(id = "3", name = "PLC & Automation", count = 3)
)

private val mockGuides = listOf(
    Course(
        id = "c1",
        title = "Complete STM32 Microcontroller Guide",
        subtitle = "Master ARM Cortex-M architecture, GPIO, Timers, and FreeRTOS from scratch.",
        author = "Eng. Ahmed Hassan",
        level = "Beginner",
        downloads = 1420,
        rating = 4.8f,
        points = 250,
        rank = 1,
        isSaved = true,
        isDownloaded = true,
        fileUrls = listOf("https://example.com/files/stm32_guide.pdf")
    ),
    Course(
        id = "c2",
        title = "Kotlin Multiplatform Architecture Patterns",
        subtitle = "Build robust production cross-platform apps using KMP, Decompose, and Room.",
        author = "Eng. Mohamed Aly",
        level = "Intermediate",
        downloads = 850,
        rating = 4.9f,
        points = 400,
        rank = 2,
        isSaved = false,
        isDownloaded = false,
        fileUrls = listOf("https://example.com/files/kmp_arch.pdf")
    ),
    Course(
        id = "c3",
        title = "Advanced Reciprocating Mechanism Design",
        subtitle = "Detailed kinematics, stress analysis, and motor sizing for cutting machines.",
        author = "Eng. Mahmoud Tarek",
        level = "Advanced",
        downloads = 310,
        rating = 4.6f,
        points = 600,
        rank = 3,
        isSaved = false,
        isDownloaded = false,
        fileUrls = emptyList()
    )
)

@MultiPlatformPreviews
@Composable
fun MyScreenPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            JuniorHomeScreenContent(
                guides = mockGuides,
                categories = mockCategories,
                onSearch = {},
                onFavoriteToggle = { _, _ -> }
            )
        }
    }
}