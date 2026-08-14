package com.learn.catalog2.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
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

enum class SortOption {
    DEFAULT,
    MOST_DOWNLOADED,
    HIGHEST_RATED,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW
}

private val FallbackCategories = listOf(
    Category(id = "1", name = "Robotics & Embedded", count = 0),
    Category(id = "2", name = "Android KMP", count = 0),
    Category(id = "3", name = "PLC & Automation", count = 0),
    Category(id = "4", name = "Mechanical Design", count = 0)
)

@Composable
fun JuniorHomeScreen(
    viewModel: ExploreViewModel = koinViewModel()
) {
    val guides by viewModel.trendingCourses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val downloadingIds by viewModel.downloadingIds.collectAsState()

    // 💡 تم حذف downloadStatus لأن GlobalPurchaseHandler أصبح يعالج التنبيهات على مستوى التطبيق بالكامل

    JuniorHomeScreenContent(
        guides = guides,
        categories = categories,
        downloadingIds = downloadingIds,
        onSearch = { query: String -> viewModel.searchGuides(query) },
        onFavoriteToggle = { id: String, isFav: Boolean -> viewModel.toggleFavorite(id, isFav) },
        onDownloadClick = { guide: Course -> viewModel.downloadGuide(guide) },
        onRateClick = { guideId: String, rating: Float -> viewModel.rateGuide(guideId, rating) }
    )
}

@Composable
fun JuniorHomeScreenContent(
    guides: List<Course>,
    categories: List<Category>,
    downloadingIds: Set<String>,
    onSearch: (String) -> Unit,
    onFavoriteToggle: (String, Boolean) -> Unit,
    onDownloadClick: (Course) -> Unit,
    onRateClick: (String, Float) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("all") }

    var isSortMenuExpanded by remember { mutableStateOf(false) }
    var currentSortOption by remember { mutableStateOf(SortOption.DEFAULT) }

    val displayCategories = remember(categories) {
        if (categories.isEmpty()) FallbackCategories else categories
    }

    val filteredAndSortedGuides = remember(guides, searchQuery, selectedCategoryId, displayCategories, currentSortOption) {
        val selectedCategory = displayCategories.find { it.id == selectedCategoryId }

        val filtered = guides.filter { guide ->
            val cleanQuery = searchQuery.trim()
            val matchesSearch = cleanQuery.isEmpty() ||
                    guide.title.contains(cleanQuery, ignoreCase = true) ||
                    guide.author.contains(cleanQuery, ignoreCase = true)

            val matchesCategory = if (selectedCategoryId == "all") {
                true
            } else {
                guide.categoryId == selectedCategoryId ||
                        (selectedCategory != null && guide.categoryName.equals(selectedCategory.name, ignoreCase = true))
            }

            matchesSearch && matchesCategory
        }

        when (currentSortOption) {
            SortOption.MOST_DOWNLOADED -> filtered.sortedByDescending { it.downloads }
            SortOption.HIGHEST_RATED -> filtered.sortedByDescending { it.rating.toString() }
            SortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { it.points }
            SortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending { it.points }
            SortOption.DEFAULT -> filtered
        }
    }

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

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newQuery ->
                    searchQuery = newQuery
                    onSearch(newQuery)
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

            // Category Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryId == "all",
                        onClick = { selectedCategoryId = "all" },
                        label = { Text(stringResource(Res.string.tab_all)) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategoryId == "all",
                            borderColor = Color.Transparent,
                            selectedBorderColor = Color.Transparent
                        )
                    )
                }

                items(displayCategories, key = { it.id }) { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = { selectedCategoryId = category.id },
                        label = { Text(category.name) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategoryId == category.id,
                            borderColor = Color.Transparent,
                            selectedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Results Header + Sort Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredAndSortedGuides.size} results",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )

                Box {
                    OutlinedButton(
                        onClick = { isSortMenuExpanded = true },
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.outline_filter_alt_24),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(Res.string.sort_filter), fontSize = 13.sp)
                    }

                    DropdownMenu(
                        expanded = isSortMenuExpanded,
                        onDismissRequest = { isSortMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("الأفتراضي (Default)") },
                            onClick = {
                                currentSortOption = SortOption.DEFAULT
                                isSortMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("الأكثر تنزيلاً (Most Downloaded)") },
                            onClick = {
                                currentSortOption = SortOption.MOST_DOWNLOADED
                                isSortMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("الأعلى تقييماً (Highest Rated)") },
                            onClick = {
                                currentSortOption = SortOption.HIGHEST_RATED
                                isSortMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("الأقل نقاطاً (Points: Low to High)") },
                            onClick = {
                                currentSortOption = SortOption.PRICE_LOW_TO_HIGH
                                isSortMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("الأعلى نقاطاً (Points: High to Low)") },
                            onClick = {
                                currentSortOption = SortOption.PRICE_HIGH_TO_LOW
                                isSortMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Feed / Empty State
            if (filteredAndSortedGuides.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد نتائج تطابق بحثك",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredAndSortedGuides, key = { it.id }) { guide ->
                        GuideListCard(
                            guide = guide,
                            isDownloading = downloadingIds.contains(guide.id),
                            onFavoriteToggle = { onFavoriteToggle(guide.id, !guide.isSaved) },
                            onDownloadClick = { onDownloadClick(guide) },
                            onRateClick = { rating -> onRateClick(guide.id, rating) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideListCard(
    guide: Course,
    isDownloading: Boolean,
    onFavoriteToggle: () -> Unit,
    onDownloadClick: () -> Unit,
    onRateClick: (Float) -> Unit
) {
    var showRatingDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 0.8.dp,
            color = Color(0xFFE2E8F0)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
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

            Column(modifier = Modifier.weight(1f)) {
                Text(guide.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(guide.author.ifEmpty { "Eng. Unknown" }, fontSize = 12.sp, color = Color.Gray)

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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

                    Text(
                        text = "★ ${guide.rating}",
                        fontSize = 11.sp,
                        color = Color(0xFFFFB300),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showRatingDialog = true }
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text("${guide.points} pts", color = Color(0xFFD97706), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))

                if (guide.isDownloaded) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✓ Saved", color = Color(0xFF00796B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = onDownloadClick,
                        enabled = !isDownloading,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                        modifier = Modifier.height(32.dp)
                    ) {
                        if (isDownloading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                painter = painterResource(Res.drawable.outline_download_2_24),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(Res.string.get_label), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (showRatingDialog) {
        RatingDialog(
            onDismiss = { showRatingDialog = false },
            onSubmitRating = { rating ->
                onRateClick(rating)
            }
        )
    }
}

@Composable
fun RatingDialog(
    onDismiss: () -> Unit,
    onSubmitRating: (Float) -> Unit
) {
    var selectedRating by remember { mutableStateOf(5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تقييم الكتالوج", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("اختر تقييمك لهذا الملف:")
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..5).forEach { index ->
                        IconButton(onClick = { selectedRating = index }) {
                            Text(
                                text = if (index <= selectedRating) "★" else "☆",
                                fontSize = 28.sp,
                                color = Color(0xFFFFB300)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSubmitRating(selectedRating.toFloat())
                onDismiss()
            }) {
                Text("إرسال")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}