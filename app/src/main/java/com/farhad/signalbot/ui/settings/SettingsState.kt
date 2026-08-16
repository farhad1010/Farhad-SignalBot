package com.farhad.signalbot.ui.settings

data class SettingsState(
    val notificationsEnabled: Boolean = true,
    val autoRefreshEnabled: Boolean = true,
    val refreshIntervalSeconds: Long = 15L,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)
