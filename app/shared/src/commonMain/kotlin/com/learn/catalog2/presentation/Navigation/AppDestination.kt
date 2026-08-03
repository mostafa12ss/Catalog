package com.learn.catalog2.presentation.Navigation

import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.baseline_explore_24
import catalog2.app.shared.generated.resources.explore
import catalog2.app.shared.generated.resources.home
import catalog2.app.shared.generated.resources.offline
import catalog2.app.shared.generated.resources.outline_account_circle_24
import catalog2.app.shared.generated.resources.outline_home_24
import catalog2.app.shared.generated.resources.outline_offline_pin_24
import catalog2.app.shared.generated.resources.profile
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class AppDestination(
    val route: String,
    val labelRes: StringResource,
    val icon: DrawableResource
) {

    data object Home : AppDestination(
        route = "home",
        labelRes = Res.string.home,
        icon = Res.drawable.outline_home_24
    )

    data object Explore : AppDestination(
        route = "explore",
        labelRes = Res.string.explore,
        icon = Res.drawable.baseline_explore_24
    )

    data object Offline : AppDestination(
        route = "offline",
        labelRes = Res.string.offline,
        icon = Res.drawable.outline_offline_pin_24
    )

    data object Profile : AppDestination(
        route = "profile",
        labelRes = Res.string.profile,
        icon = Res.drawable.outline_account_circle_24
    )

    companion object {
        val bottomNavItems by lazy {
            listOf(
                Home,
                Explore,
                Offline,
                Profile
            )
        }
    }
}