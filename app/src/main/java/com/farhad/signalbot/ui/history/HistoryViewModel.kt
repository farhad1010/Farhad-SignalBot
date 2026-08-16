package com.farhad.signalbot.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farhad.signalbot.data.SignalHistoryRepository
import com.farhad.signalbot.domain.PerformanceStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: SignalHistoryRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(HistoryUiState())

    val uiState: StateFlow<HistoryUiState> =
        _uiState.asStateFlow()

    init {
        observeHistory()
    }

    private fun observeHistory() {
        viewModelScope.launch {
            repository.history.collect { records ->

                _uiState.value =
                    HistoryUiState(
                        records = records,
                        statistics =
                            PerformanceStats.from(records),
                        isLoading = false
                    )
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clear()
        }
    }
}
