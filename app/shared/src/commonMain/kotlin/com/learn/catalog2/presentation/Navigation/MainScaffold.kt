package com.learn.catalog2.presentation.Navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.learn.catalog2.domain.models.UserRole
import com.learn.catalog2.presentation.screens.*
import com.learn.catalog2.presentation.viewmodels.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val profileViewModel: ProfileViewModel = koinViewModel()
    val currentRole by profileViewModel.currentRole.collectAsState()
    val pointsBalance by profileViewModel.pointsBalance.collectAsState()

    val screenTitle = when (currentRoute) {
        AppDestination.Home.route -> if (currentRole == UserRole.JUNIOR) "Learn & Download" else "Provide Guides"
        AppDestination.Explore.route -> "Explore"
        AppDestination.Offline.route -> "Offline Library"
        AppDestination.Profile.route -> "My Profile"
        "add_catalog" -> "Add Catalog"
        "wallet" -> "My Wallet"
        else -> ""
    }

    Scaffold(
        topBar = {
            // فحص ما إذا كانت الشاشة الحالية شاشة فرعية تحتاج زر رجوع
            val isSubScreen = currentRoute == "wallet" || currentRoute == "add_catalog"

            AppTopBar(
                screenTitle = screenTitle,
                userRole = currentRole,
                pointsBalance = pointsBalance,
                showBackButton = isSubScreen,
                onBackClick = {
                    // 👈 تنفيذ العودة أينما كان المستخدم
                    navController.popBackStack()
                },
                onRoleClick = { profileViewModel.toggleRole() },
                onWalletClick = {
                    if (currentRoute != "wallet") {
                        navController.navigate("wallet")
                    }
                }
            )
        },
        bottomBar = {
            if (currentRoute != "add_catalog" && currentRoute != "wallet") {
                AppBottomBar(navController)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 840.dp)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = AppDestination.Home.route
                ) {
                    composable(AppDestination.Home.route) {
                        HomeScreen(
                            role = currentRole,
                            onAddCatalogClick = { navController.navigate("add_catalog") }
                        )
                    }
                    composable(AppDestination.Explore.route) {
                        ExploreScreen(navController = navController)
                    }
                    composable(AppDestination.Offline.route) {
                        OfflineScreen()
                    }
                    composable(AppDestination.Profile.route) {
                        ProfileScreen(navController = navController)
                    }
                    composable("add_catalog") {
                        AddNewCatalogScreen(onDismiss = { navController.popBackStack() })
                    }

                    // 💡 الربط الصحيح لـ WalletScreen
                    composable("wallet") {
                        WalletScreen(
                            onTopUpClick = {
                                // أضف منطق فتح نافذة الشحن هنا عند الحاجة
                            },
                            onWithdrawClick = {
                                // أضف منطق عملية السحب هنا عند الحاجة
                            }
                        )
                    }
                }
            }
        }
    }
}