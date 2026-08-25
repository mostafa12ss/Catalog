package com.learn.catalog2.presentation.Navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.compositionLocalOf
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
import com.learn.catalog2.presentation.viewmodels.WalletViewModel
import org.koin.compose.viewmodel.koinViewModel

// Local composition لتمرير الـ Padding السفلي للشاشات بسهولة
val LocalBottomPadding = compositionLocalOf { 0.dp }

@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val profileViewModel: ProfileViewModel = koinViewModel()
    val walletViewModel: WalletViewModel = koinViewModel()

    val currentRole by profileViewModel.currentRole.collectAsState()
    val pointsBalance by walletViewModel.pointsBalance.collectAsState()

    val screenTitle = when (currentRoute) {
        AppDestination.Home.route -> if (currentRole == UserRole.JUNIOR) "Learn & Download" else "Provide Guides"
        AppDestination.Explore.route -> "Explore"
        AppDestination.Offline.route -> "Offline Library"
        AppDestination.Profile.route -> "My Profile"
        "add_catalog" -> "Add Catalog"
        "wallet" -> "My Wallet"
        else -> ""
    }

    val showBottomBar = currentRoute != "add_catalog" && currentRoute != "wallet"

    Scaffold(
        // 🟢 تلوين خلفية التطبيق تلقائياً من الـ Theme (سواء Dark أو Light)
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            val isSubScreen = currentRoute == "wallet" || currentRoute == "add_catalog"

            AppTopBar(
                screenTitle = screenTitle,
                userRole = currentRole,
                pointsBalance = pointsBalance,
                showBackButton = isSubScreen,
                onBackClick = {
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
            if (showBottomBar) {
                AppBottomBar(navController)
            }
        }
    ) { innerPadding ->
        // حساب الـ Padding السفلي فقط للشاشات لمنع غرق آخر عنصر تحت الشريط الطافي
        val bottomPadding = if (showBottomBar) 80.dp else 0.dp

        CompositionLocalProvider(LocalBottomPadding provides bottomPadding) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
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
                        composable("wallet") {
                            WalletScreen(viewModel = walletViewModel)
                        }
                    }
                }
            }
        }
    }
}