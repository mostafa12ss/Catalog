package com.learn.catalog2

import androidx.compose.runtime.*
import com.learn.catalog2.presentation.Navigation.MainScaffold
import com.learn.catalog2.theme.AppDirectionProvider
import com.learn.catalog2.theme.AppEnvironment
import com.learn.catalog2.theme.CatalogTheme
import com.learn.catalog2.theme.LocalAppLocale
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.learn.catalog2.presentation.screens.AuthScreen
import com.learn.catalog2.presentation.screens.DownloadScreen
import com.learn.catalog2.presentation.screens.OnboardingScreen
import com.learn.catalog2.presentation.viewmodels.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    // Observe global settings
    val isDark = AppSettings.isDarkMode
    val currentLocale = AppSettings.language

    CatalogTheme(darkTheme = isDark) {
        AppEnvironment {
            val isArabic = currentLocale.startsWith("ar")
            AppDirectionProvider(isArabic = isArabic) {
                RootNavigation()
            }
        }
    }
}

@Composable
private fun RootNavigation() {
    val rootNavController = rememberNavController()
    val authViewModel: AuthViewModel = koinViewModel()

    val currentUserState by authViewModel.currentUser.collectAsState(initial = null)

    LaunchedEffect(currentUserState) {
        val user = currentUserState
        val currentRoute = rootNavController.currentBackStackEntry?.destination?.route

        // تجاهل التوجيه التلقائي إذا كان المستخدم حالياً في شاشة التنزيل
        if (currentRoute == "download") return@LaunchedEffect

        if (user != null && currentRoute != "main") {
            rootNavController.navigate("main") {
                popUpTo(0) { inclusive = true }
            }
        } else if (user == null && currentRoute == "main") {
            rootNavController.navigate("auth") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = rootNavController,
        startDestination = "download" // ⚡ البداية بشاشة التنزيل للويب
    ) {
        // ⚡ شاشة التنزيل المخصصة للويب
        composable("download") {
            DownloadScreen(
                onNavigateToWebApp = {
                    val targetDestination = if (currentUserState != null) "main" else "onboarding"
                    rootNavController.navigate(targetDestination) {
                        popUpTo("download") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onGetStarted = {
                    rootNavController.navigate("auth") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("auth") {
            AuthScreen(
                onAuthenticated = {
                    rootNavController.navigate("main") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            GlobalPurchaseHandler(
                onNavigateToWallet = {
                    rootNavController.navigate("main")
                }
            ) {
                MainScaffold()
            }
        }
    }
}