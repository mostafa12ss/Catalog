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
import com.learn.catalog2.presentation.Navigation.LocalBottomPadding
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
    val selectedCategory by viewModel.selectedCategoryId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    JuniorHomeScreenContent(
        guides = guides,
        categories = categories,
        selectedCategoryId = selectedCategory,
        searchQuery = searchQuery,
        downloadingIds = downloadingIds,
        onCategorySelect = { categoryId -> viewModel.selectCategory(categoryId) },
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
    selectedCategoryId: String?,
    searchQuery: String,
    downloadingIds: Set<String>,
    onCategorySelect: (String) -> Unit,
    onSearch: (String) -> Unit,
    onFavoriteToggle: (String, Boolean) -> Unit,
    onDownloadClick: (Course) -> Unit,
    onRateClick: (String, Float) -> Unit
) {
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    var currentSortOption by remember { mutableStateOf(SortOption.DEFAULT) }

    val bottomPadding = LocalBottomPadding.current

    val displayCategories = remember(categories) {
        if (categories.isEmpty()) FallbackCategories else categories
    }

    // 🟢 الترتيب المحلي المباشر بناءً على البيانات القادمة من الـ ViewModel
    val sortedGuides = remember(guides, currentSortOption) {
        when (currentSortOption) {
            SortOption.MOST_DOWNLOADED -> guides.sortedByDescending { it.downloads }
            SortOption.HIGHEST_RATED -> guides.sortedByDescending { it.rating }
            SortOption.PRICE_LOW_TO_HIGH -> guides.sortedBy { it.points }
            SortOption.PRICE_HIGH_TO_LOW -> guides.sortedByDescending { it.points }
            SortOption.DEFAULT -> guides
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearch,
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
                val isAllSelected = selectedCategoryId == null
                FilterChip(
                    selected = isAllSelected,
                    onClick = { if (!isAllSelected) onCategorySelect("") },
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
                        selected = isAllSelected,
                        borderColor = Color.Transparent,
                        selectedBorderColor = Color.Transparent
                    )
                )
            }

            items(displayCategories, key = { it.id }) { category ->
                val isSelected = selectedCategoryId.equals(category.id, ignoreCase = true) ||
                        selectedCategoryId.equals(category.name, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelect(category.id) },
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
                        selected = isSelected,
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
                text = stringResource(Res.string.results_count, sortedGuides.size),
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
                        text = { Text(stringResource(Res.string.sort_default)) },
                        onClick = {
                            currentSortOption = SortOption.DEFAULT
                            isSortMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.sort_most_downloaded)) },
                        onClick = {
                            currentSortOption = SortOption.MOST_DOWNLOADED
                            isSortMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.sort_highest_rated)) },
                        onClick = {
                            currentSortOption = SortOption.HIGHEST_RATED
                            isSortMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.sort_price_low)) },
                        onClick = {
                            currentSortOption = SortOption.PRICE_LOW_TO_HIGH
                            isSortMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.sort_price_high)) },
                        onClick = {
                            currentSortOption = SortOption.PRICE_HIGH_TO_LOW
                            isSortMenuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Empty State & List
        if (sortedGuides.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.no_results),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(
                    top = 4.dp,
                    bottom = bottomPadding + 24.dp
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sortedGuides, key = { it.id }) { guide ->
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
                Text(
                    text = guide.author.ifEmpty { stringResource(Res.string.eng_unknown) },
                    fontSize = 12.sp,
                    color = Color.Gray
                )

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

                    Text("${guide.downloads} ${stringResource(Res.string.dl_unit)}", fontSize = 11.sp, color = Color.Gray)

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
                Text(
                    text = "${guide.points} ${stringResource(Res.string.pts_unit)}",
                    color = Color(0xFFD97706),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(8.dp))

                if (guide.isDownloaded) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(Res.string.saved),
                            color = Color(0xFF00796B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
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
        title = { Text(stringResource(Res.string.rate_catalog_title), fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(Res.string.rate_catalog_desc))
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
                Text(stringResource(Res.string.send))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}