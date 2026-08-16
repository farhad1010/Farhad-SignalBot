package com.farhad.signalbot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.farhad.signalbot.core.di.AppContainer
import com.farhad.signalbot.data.SignalHistoryRepository
import com.farhad.signalbot.ui.SignalDashboardScreen
import com.farhad.signalbot.ui.history.HistoryScreen
import com.farhad.signalbot.ui.settings.SettingsScreen

@Composable
fun SignalBotNavHost(
    navController: NavHostController,
    container: AppContainer,
    historyRepository: SignalHistoryRepository
) {
    NavHost(
        navController = navController,
        startDestination =
            AppDestination.Dashboard.route
    ) {

        composable(
            AppDestination.Dashboard.route
        ) {
            SignalDashboardScreen(
                container = container
            )
        }

        composable(
            AppDestination.Signals.route
        ) {
            SignalDashboardScreen(
                container = container
            )
        }

        composable(
            AppDestination.History.route
        ) {
            HistoryScreen(
                repository = historyRepository
            )
        }

        composable(
            AppDestination.Settings.route
        ) {
            SettingsScreen()
        }
    }
}
