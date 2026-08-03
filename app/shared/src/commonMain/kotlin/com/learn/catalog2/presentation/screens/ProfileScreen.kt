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
import com.learn.catalog2.presentation.Navigation.AppBottomBar
import org.koin.compose.viewmodel.koinViewModel
import com.learn.catalog2.presentation.Navigation.AppTopBar
import com.learn.catalog2.presentation.viewmodels.ProfileViewModel
import com.learn.catalog2.presentation.components.AccountItem
import com.learn.catalog2.presentation.components.CurrentModeSection
import com.learn.catalog2.presentation.components.StatsSection
import com.learn.catalog2.presentation.components.UserHeader

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val user by viewModel.user.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val points by viewModel.pointsBalance.collectAsState()
    val isSeniorMode by viewModel.isSeniorMode.collectAsState()

    Scaffold(
//        topBar = {
//            AppTopBar(
//                screenTitle = "Profile",
//                userRole = currentRole,
//                pointsBalance = points,
//                onRoleClick = { viewModel.toggleRole() }
//            )
//        },
//        bottomBar = {
//            AppBottomBar(navController = navController)
//        },
//        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            // User Info
            item {
                UserHeader(user = user, isSeniorMode = isSeniorMode)
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Current Mode Toggle
            item {
                CurrentModeSection(
                    isSeniorMode = isSeniorMode,
                    onModeChange = { viewModel.changeMode(it) }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Stats Cards
            item {
                StatsSection(user = user, isSeniorMode = isSeniorMode)
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // Account Section
            item {
                Text(
                    text = "ACCOUNT",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Account Items
            items(listOf(
                "Notifications" to "On",
                "Offline Sync" to "Auto",
                "Privacy & Data" to "",
                "Language" to "English",
                "App Preferences" to ""
            )) { (title, value) ->
                AccountItem(title = title, value = value)
            }
        }
    }
}