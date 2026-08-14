package com.learn.catalog2.presentation.screens

import androidx.compose.runtime.Composable
import com.learn.catalog2.domain.models.UserRole

@Composable
fun HomeScreen(
    role: UserRole,
    onAddCatalogClick: () -> Unit
) {
    when (role) {
        UserRole.JUNIOR -> {
            JuniorHomeScreen()
        }
        UserRole.SENIOR -> {
            SeniorHomeScreen(
                onAddCatalogClick = onAddCatalogClick
            )
        }
    }
}