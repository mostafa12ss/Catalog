package com.learn.catalog2.presentation.screens

import androidx.navigation.NavController
import com.learn.catalog2.presentation.Navigation.AppTopBar
import com.learn.catalog2.presentation.viewmodels.ExploreViewModel
import com.learn.catalog2.presentation.components.TrendingCourseItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.learn.catalog2.presentation.Navigation.AppBottomBar
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.explore
import catalog2.app.shared.generated.resources.trending
import catalog2.app.shared.generated.resources.by_category
import com.learn.catalog2.presentation.components.CategoryItem

@Composable
fun ExploreScreen(
    navController: NavController,
    viewModel: ExploreViewModel = koinViewModel()
) {
    val trendingCourses by viewModel.trendingCourses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val points by viewModel.pointsBalance.collectAsState()

    Scaffold(
//        topBar = {
//            AppTopBar(
//                screenTitle = stringResource(Res.string.explore),
//                userRole = userRole,
//                pointsBalance = points,
//                onRoleClick = { /* TODO: Change role dialog */ }
//            )
//        },
//        bottomBar = {
//            AppBottomBar(navController = navController)
//        },
//        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                // TRENDING THIS WEEK
                Text(
                    text = stringResource(Res.string.trending),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(trendingCourses) { course ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    TrendingCourseItem(course = course)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                // BROWSE BY CATEGORY
                Text(
                    text = stringResource(Res.string.by_category),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(categories) { category ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    CategoryItem(category = category)
                }
            }
        }
    }
}
