package com.learn.catalog2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.learn.catalog2.presentation.Navigation.MainScaffold
import com.learn.catalog2.presentation.screens.AuthScreen
import com.learn.catalog2.presentation.screens.OnboardingScreen
import com.learn.catalog2.presentation.viewmodels.AuthViewModel
import com.learn.catalog2.theme.AppDirectionProvider
import com.learn.catalog2.theme.AppEnvironment
import com.learn.catalog2.theme.CatalogTheme
import com.learn.catalog2.theme.LocalAppLocale
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    var isDark by remember { mutableStateOf(false) }

    CatalogTheme(darkTheme = isDark) {
        AppEnvironment {
            val isArabic = LocalAppLocale.current.startsWith("ar")
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

    // استخدام State لمعرفة إذا ما كانت بيانات المستخدم جاري تحميلها أم تحققت
    val currentUserState by authViewModel.currentUser.collectAsState(initial = null)

    // متابعة التنقل الذكي بناءً على تغير حالة المستخدم
    LaunchedEffect(currentUserState) {
        val user = currentUserState
        val currentRoute = rootNavController.currentBackStackEntry?.destination?.route

        if (user != null && currentRoute != "main") {
            rootNavController.navigate("main") {
                popUpTo(0) { inclusive = true }
            }
        } else if (user == null && currentRoute == "main") {
            // تسجيل الخروج التلقائي في حال إلغاء الجلسة
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
            MainScaffold()
        }
    }
}