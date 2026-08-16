package com.farhad.signalbot.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.farhad.signalbot.data.SignalHistoryRepository

class HistoryViewModelFactory(
    private val repository: SignalHistoryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                HistoryViewModel::class.java
            )
        ) {
            return HistoryViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel: ${modelClass.name}"
        )
    }
}
