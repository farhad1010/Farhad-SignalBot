package com.farhad.signalbot.core.model

sealed interface SignalState {

    data object Idle : SignalState

    data object Loading : SignalState

    data class Ready(
        val signal: TradingSignal,
        val analysis: TechnicalSnapshot
    ) : SignalState

    data class NoSignal(
        val analysis: TechnicalSnapshot
    ) : SignalState

    data class Error(
        val message: String
    ) : SignalState
}
