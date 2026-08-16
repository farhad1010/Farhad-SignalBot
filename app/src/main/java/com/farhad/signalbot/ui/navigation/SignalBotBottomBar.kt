package com.farhad.signalbot.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun SignalBotBottomBar(
    navController: NavHostController
) {
    val backStackEntry =
        navController
            .currentBackStackEntryAsState()

    val currentRoute =
        backStackEntry.value?.destination?.route

    NavigationBar {

        AppDestination.bottomItems.forEach { destination ->

            NavigationBarItem(
                selected =
                    currentRoute == destination.route,

                onClick = {

                    navController.navigate(
                        destination.route
                    ) {
                        popUpTo(
                            AppDestination.Dashboard.route
                        ) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                },

                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription =
                            destination.label
                    )
                },

                label = {
                    Text(destination.label)
                }
            )
        }
    }
}
