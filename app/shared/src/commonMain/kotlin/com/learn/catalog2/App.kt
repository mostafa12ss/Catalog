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
        startDestination = "onboarding"
    ) {
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
            // 💡 تغليف شاشات التطبيق الأساسية بالـ GlobalPurchaseHandler
            GlobalPurchaseHandler(
                onNavigateToWallet = {
                    // التوجيه لشاشة المحفظة داخل الـ MainScaffold أو عبر الـ rootNavController
                    rootNavController.navigate("main") // أو إرسال event للتنقيل لتاب المحفظة
                }
            ) {
                MainScaffold()
            }
        }
    }
}