package com.farhad.signalbot.domain

import com.farhad.signalbot.core.model.SignalDirection
import com.farhad.signalbot.core.model.SignalState
import com.farhad.signalbot.core.model.TradingSignal
import com.farhad.signalbot.core.model.TradingSymbol
import java.time.Duration
import java.time.Instant
import java.util.UUID

class SignalFactory {

    fun create(
        symbol: TradingSymbol,
        analysis: SignalAnalysisResult,
        timeframeSeconds: Long,
        now: Instant = Instant.now()
    ): SignalState {

        if (analysis.direction == SignalDirection.NEUTRAL) {
            return SignalState.NoSignal(
                analysis = analysis.snapshot
            )
        }

        val expiresAt =
            now.plus(
                Duration.ofSeconds(timeframeSeconds)
            )

        val signal = TradingSignal(
            id = UUID.randomUUID().toString(),
            symbol = symbol,
            direction = analysis.direction,
            strength = analysis.strength,
            confidence = analysis.confidence,
            generatedAt = now,
            sourcePrice = analysis.snapshot.currentPrice,
            timeframeSeconds = timeframeSeconds,
            expiresAt = expiresAt,
            reasons = analysis.reasons
        )

        return SignalState.Ready(
            signal = signal,
            analysis = analysis.snapshot
        )
    }
}
