package com.farhad.signalbot.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Dashboard : AppDestination(
        route = "dashboard",
        label = "Dashboard",
        icon = Icons.Default.Home
    )

    data object Signals : AppDestination(
        route = "signals",
        label = "Signals",
        icon = Icons.Default.ShowChart
    )

    data object History : AppDestination(
        route = "history",
        label = "History",
        icon = Icons.Default.History
    )

    data object Settings : AppDestination(
        route = "settings",
        label = "Settings",
        icon = Icons.Default.Settings
    )

    companion object {
        val bottomItems = listOf(
            Dashboard,
            Signals,
            History,
            Settings
        )
    }
}
