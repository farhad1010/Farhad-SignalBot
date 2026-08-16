package com.farhad.signalbot.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel =
        viewModel()

    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "Settings",
            style =
                MaterialTheme.typography.headlineMedium
        )

        SettingsSwitch(
            title = "Signal Notifications",
            subtitle =
                "Receive notifications when a new signal is generated.",
            checked = state.notificationsEnabled,
            onCheckedChange =
                viewModel::setNotificationsEnabled
        )

        SettingsSwitch(
            title = "Automatic Refresh",
            subtitle =
                "Automatically request fresh market candles.",
            checked = state.autoRefreshEnabled,
            onCheckedChange =
                viewModel::setAutoRefreshEnabled
        )

        SettingsSwitch(
            title = "Signal Sound",
            subtitle =
                "Play a sound for important signal events.",
            checked = state.soundEnabled,
            onCheckedChange =
                viewModel::setSoundEnabled
        )

        SettingsSwitch(
            title = "Vibration",
            subtitle =
                "Use device vibration for signal alerts.",
            checked = state.vibrationEnabled,
            onCheckedChange =
                viewModel::setVibrationEnabled
        )

        Text(
            text =
                "Refresh interval: " +
                "${state.refreshIntervalSeconds} seconds"
        )
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Text(
                    text = subtitle,
                    style =
                        MaterialTheme.typography.bodySmall
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
