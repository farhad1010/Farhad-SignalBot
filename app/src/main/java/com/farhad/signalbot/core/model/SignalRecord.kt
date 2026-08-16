package com.farhad.signalbot.core.model

import java.time.Instant

data class SignalRecord(
    val signal: TradingSignal,
    val outcome: SignalOutcome = SignalOutcome.PENDING,
    val evaluatedAt: Instant? = null,
    val resultPrice: Double? = null
) {
    val isFinished: Boolean
        get() = outcome != SignalOutcome.PENDING
}
