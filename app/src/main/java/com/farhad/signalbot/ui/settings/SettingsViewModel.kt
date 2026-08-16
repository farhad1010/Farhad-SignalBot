package com.farhad.signalbot.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _state =
        MutableStateFlow(SettingsState())

    val state: StateFlow<SettingsState> =
        _state.asStateFlow()

    fun setNotificationsEnabled(enabled: Boolean) {
        _state.value =
            _state.value.copy(
                notificationsEnabled = enabled
            )
    }

    fun setAutoRefreshEnabled(enabled: Boolean) {
        _state.value =
            _state.value.copy(
                autoRefreshEnabled = enabled
            )
    }

    fun setSoundEnabled(enabled: Boolean) {
        _state.value =
            _state.value.copy(
                soundEnabled = enabled
            )
    }

    fun setVibrationEnabled(enabled: Boolean) {
        _state.value =
            _state.value.copy(
                vibrationEnabled = enabled
            )
    }

    fun setRefreshInterval(seconds: Long) {
        require(seconds in 5L..300L)

        _state.value =
            _state.value.copy(
                refreshIntervalSeconds = seconds
            )
    }
}
