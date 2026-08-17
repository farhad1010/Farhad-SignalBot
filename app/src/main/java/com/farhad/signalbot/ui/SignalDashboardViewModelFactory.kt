package com.farhad.signalbot.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.farhad.signalbot.core.di.AppContainer
import com.farhad.signalbot.data.SignalHistoryRepository

class SignalDashboardViewModelFactory(
    private val container: AppContainer,
    private val historyRepository: SignalHistoryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                SignalDashboardViewModel::class.java
            )
        ) {

            return SignalDashboardViewModel(
                container = container,
                historyRepository =
                    historyRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel: ${modelClass.name}"
        )
    }
}
