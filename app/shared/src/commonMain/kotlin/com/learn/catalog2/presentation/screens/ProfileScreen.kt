package com.learn.catalog2.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.learn.catalog2.domain.models.AppUser
import com.learn.catalog2.presentation.components.AccountSettingRow
import com.learn.catalog2.presentation.components.CurrentModeSection
import com.learn.catalog2.presentation.components.StatsSection
import com.learn.catalog2.presentation.components.UserHeader
import com.learn.catalog2.presentation.viewmodels.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val userNullable by viewModel.user.collectAsState()
    val isSeniorMode by viewModel.isSeniorMode.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 💡 1. قراءة حالة الإحصائيات الحقيقية المربوطة بـ SQLDelight
    val userStats by viewModel.userStats.collectAsState()

    val isOfflineSync by viewModel.isOfflineSync.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    Scaffold { padding ->
        if (isLoading && userNullable == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val user = userNullable ?: AppUser.getDemoUser()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                // User Header
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

                // 💡 2. تمرير الـ stats الحقيقية المحدثة إلى StatsSection
                item {
                    StatsSection(
                        user = user,
                        isSeniorMode = isSeniorMode,
                        stats = userStats // 👈 إذا كانت StatsSection تدعم استقبال كائن UserProfileStats
                    )
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }

                // Account Section Title
                item {
                    Text(
                        text = "ACCOUNT",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // 1. Offline Sync (Switch)
                item {
                    AccountSettingRow(
                        title = "Offline Sync",
                        isSwitch = true,
                        switchState = isOfflineSync,
                        onSwitchChange = { viewModel.toggleOfflineSync(it) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }

                // 2. Dark Mode (Switch)
                item {
                    AccountSettingRow(
                        title = "Dark Theme",
                        isSwitch = true,
                        switchState = isDarkMode,
                        onSwitchChange = { viewModel.toggleDarkMode(it) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }

                // 3. Language Selector
                item {
                    AccountSettingRow(
                        title = "Language",
                        valueText = currentLanguage,
                        onClick = { viewModel.toggleLanguage() }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }

                // 4. Log Out Button
                item {
                    AccountSettingRow(
                        title = "Log Out",
                        isDestructive = true,
                        onClick = {
                            viewModel.logout {
                                // أضف كود التنقل لشاشة الترحيب/تسجيل الدخول عند تسجيل الخروج
                            }
                        }
                    )
                }
            }
        }
    }
}