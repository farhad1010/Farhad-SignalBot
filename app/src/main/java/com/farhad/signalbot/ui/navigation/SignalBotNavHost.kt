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
        startDestination = AppDestination.Dashboard.route
    ) {

        /*
         * Main real-time market dashboard.
         *
         * This screen receives both the application
         * container and persistent signal-history repository.
         */
        composable(
            route = AppDestination.Dashboard.route
        ) {
            SignalDashboardScreen(
                container = container,
                historyRepository = historyRepository
            )
        }

        /*
         * Dedicated Signals screen.
         *
         * It uses the same real market-analysis pipeline,
         * but is a separate navigation destination.
         */
        composable(
            route = AppDestination.Signals.route
        ) {
            SignalDashboardScreen(
                container = container,
                historyRepository = historyRepository
            )
        }

        /*
         * Persistent signal history.
         */
        composable(
            route = AppDestination.History.route
        ) {
            HistoryScreen(
                repository = historyRepository
            )
        }

        /*
         * Application settings.
         */
        composable(
            route = AppDestination.Settings.route
        ) {
            SettingsScreen()
        }
    }
}
