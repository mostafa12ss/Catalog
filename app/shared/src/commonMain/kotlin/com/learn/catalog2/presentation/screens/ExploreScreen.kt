package com.learn.catalog2.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.by_category
import catalog2.app.shared.generated.resources.trending
import com.learn.catalog2.presentation.components.CategoryItem
import com.learn.catalog2.presentation.components.TrendingCourseItem
import com.learn.catalog2.presentation.viewmodels.ExploreViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExploreScreen(
    navController: NavController,
    viewModel: ExploreViewModel = koinViewModel()
) {
    val trendingCourses by viewModel.trendingCourses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()

    // 💡 تم حذف معالجة الـ Dialog والـ Snackbar هنا لأن GlobalPurchaseHandler يتولاها مركزياً

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.trending),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (trendingCourses.isEmpty()) {
                item {
                    Text(
                        text = "No catalogs available right now.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else {
                items(trendingCourses, key = { it.id }) { course ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        TrendingCourseItem(
                            course = course,
                            onClick = {
                                // ⚡ يستدعي دالة الشراء والتنزيل، والـ GlobalPurchaseHandler سيعالج النتيجة تلقائياً
                                viewModel.downloadGuide(course)
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(Res.string.by_category),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(categories, key = { it.id }) { category ->
                val isSelected = category.id == selectedCategoryId

                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    CategoryItem(
                        category = category,
                        isSelected = isSelected,
                        onClick = {
                            viewModel.selectCategory(category.id)
                        }
                    )
                }
            }
        }
    }
}