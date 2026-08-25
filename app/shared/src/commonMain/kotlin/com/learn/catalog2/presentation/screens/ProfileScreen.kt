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
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.account_header
import catalog2.app.shared.generated.resources.dark_theme
import catalog2.app.shared.generated.resources.language_label
import catalog2.app.shared.generated.resources.logout_label
import catalog2.app.shared.generated.resources.offline_sync
import com.learn.catalog2.domain.models.AppUser
import com.learn.catalog2.presentation.Navigation.LocalBottomPadding
import com.learn.catalog2.presentation.components.AccountSettingRow
import com.learn.catalog2.presentation.components.CurrentModeSection
import com.learn.catalog2.presentation.components.StatsSection
import com.learn.catalog2.presentation.components.UserHeader
import com.learn.catalog2.presentation.viewmodels.ProfileViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val userNullable by viewModel.user.collectAsState()
    val isSeniorMode by viewModel.isSeniorMode.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Read real user statistics from SQLDelight / قراءة بيانات الإحصائيات الحقيقية
    val userStats by viewModel.userStats.collectAsState()

    val isOfflineSync by viewModel.isOfflineSync.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    // 🟢 جلب قيمة الـ Padding السفلي الخاصة بالبار الطافي من الـ CompositionLocal
    val bottomPadding = LocalBottomPadding.current

    if (isLoading && userNullable == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val user = userNullable ?: AppUser.getDemoUser()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = bottomPadding + 24.dp // 🟢 رفع زر تسجيل الخروج وآخر الإعدادات فوق البار بوضوح
            )
        ) {
            // User Header / رأس صفحة المستخدم
            item {
                UserHeader(user = user, isSeniorMode = isSeniorMode)
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Current Mode Toggle / تبديل الوضع الحسابي
            item {
                CurrentModeSection(
                    isSeniorMode = isSeniorMode,
                    onModeChange = { viewModel.changeMode(it) }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Pass user statistics / تمرير الإحصائيات الحقيقية
            item {
                StatsSection(
                    user = user,
                    isSeniorMode = isSeniorMode,
                    stats = userStats
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // Account Section Title / عنوان قسم الحساب
            item {
                Text(
                    text = stringResource(Res.string.account_header),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // 1. Offline Sync (Switch) / المزامنة بدون إنترنت
            item {
                AccountSettingRow(
                    title = stringResource(Res.string.offline_sync),
                    isSwitch = true,
                    switchState = isOfflineSync,
                    onSwitchChange = { viewModel.toggleOfflineSync(it) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }

            // 2. Dark Mode (Switch) / الوضع الداكن
            item {
                AccountSettingRow(
                    title = stringResource(Res.string.dark_theme),
                    isSwitch = true,
                    switchState = isDarkMode,
                    onSwitchChange = { viewModel.toggleDarkMode(it) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }

            // 3. Language Selector / اختيارات اللغة
            item {
                AccountSettingRow(
                    title = stringResource(Res.string.language_label),
                    valueText = currentLanguage,
                    onClick = { viewModel.toggleLanguage() }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }

            // 4. Log Out Button / زر تسجيل الخروج
            item {
                AccountSettingRow(
                    title = stringResource(Res.string.logout_label),
                    isDestructive = true,
                    onClick = {
                        viewModel.logout {
                            // Navigate to auth or welcome screen / التنقل إلى شاشة تسجيل الدخول
                        }
                    }
                )
            }
        }
    }
}