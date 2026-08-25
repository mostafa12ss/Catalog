package com.learn.catalog2.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.by_category
import catalog2.app.shared.generated.resources.no_catalogs_available
import catalog2.app.shared.generated.resources.trending
import com.learn.catalog2.presentation.Navigation.LocalBottomPadding
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

    // 🟢 جلب قيمة الـ Padding المخصصة للبار الطافي من MainScaffold
    val bottomPadding = LocalBottomPadding.current

    val courses by viewModel.trendingCourses.collectAsState()

    // 2. طباعة الحجم للتأكد في الـ Console
    LaunchedEffect(courses) {
        println("🔍 Courses size in UI: ${courses.size}")
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = bottomPadding + 24.dp // 🟢 مسافة تضمن سحب آخر عنصر فوق البار بوضوح
        )
    ) {
        item {
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
                    text = stringResource(Res.string.no_catalogs_available),
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