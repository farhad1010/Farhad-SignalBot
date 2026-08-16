package com.farhad.signalbot.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.farhad.signalbot.core.di.AppContainer

class SignalDashboardViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                SignalDashboardViewModel::class.java
            )
        ) {
            return SignalDashboardViewModel(
                container
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel: ${modelClass.name}"
        )
    }
}
