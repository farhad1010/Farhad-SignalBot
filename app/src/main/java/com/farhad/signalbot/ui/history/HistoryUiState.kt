package com.farhad.signalbot.ui.history

import com.farhad.signalbot.core.model.SignalRecord
import com.farhad.signalbot.domain.PerformanceStats

data class HistoryUiState(
    val records: List<SignalRecord> = emptyList(),
    val statistics: PerformanceStats =
        PerformanceStats(
            total = 0,
            wins = 0,
            losses = 0,
            pending = 0,
            cancelled = 0
        ),
    val isLoading: Boolean = true
)
