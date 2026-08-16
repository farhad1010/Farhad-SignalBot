package com.farhad.signalbot.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.farhad.signalbot.core.di.AppContainer
import com.farhad.signalbot.data.SignalHistoryRepository
import com.farhad.signalbot.ui.navigation.SignalBotBottomBar
import com.farhad.signalbot.ui.navigation.SignalBotNavHost
import com.farhad.signalbot.ui.theme.SignalBotTheme

@Composable
fun SignalBotApp(
    container: AppContainer,
    historyRepository: SignalHistoryRepository
) {
    val navController =
        rememberNavController()

    SignalBotTheme {

        Scaffold(
            modifier = Modifier.fillMaxSize(),

            bottomBar = {
                SignalBotBottomBar(
                    navController = navController
                )
            }
        ) { paddingValues ->

            SignalBotNavHost(
                navController = navController,
                container = container,
                historyRepository = historyRepository
            )
        }
    }
}
