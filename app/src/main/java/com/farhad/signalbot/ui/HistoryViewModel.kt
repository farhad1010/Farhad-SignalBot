package com.farhad.signalbot.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farhad.signalbot.core.model.SignalOutcome
import com.farhad.signalbot.data.SignalHistoryStore
import com.farhad.signalbot.domain.PerformanceStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(
    private val store: SignalHistoryStore
) : ViewModel() {

    val records = store.signals

    val statistics: StateFlow<PerformanceStats> =
        records
            .map(PerformanceStats::from)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PerformanceStats(
                    total = 0,
                    wins = 0,
                    losses = 0,
                    pending = 0,
                    cancelled = 0
                )
            )

    fun clearHistory() {
        viewModelScope.launchSafe {
            store.clear()
        }
    }

    private fun kotlinx.coroutines.CoroutineScope.launchSafe(
        block: suspend () -> Unit
    ) {
        kotlinx.coroutines.launch {
            block()
        }
    }
}
