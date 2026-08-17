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
        now: Instant = Instant.now()
    ): SignalState {

        if (
            analysis.direction ==
            SignalDirection.NEUTRAL
        ) {

            return SignalState.NoSignal(
                analysis =
                    analysis.snapshot
            )
        }

        val seconds =
            when (
                analysis.recommendedSeconds
            ) {

                10L -> 10L
                30L -> 30L
                60L -> 60L

                else ->
                    30L
            }

        val signal =
            TradingSignal(
                id =
                    UUID.randomUUID()
                        .toString(),

                symbol =
                    symbol,

                direction =
                    analysis.direction,

                strength =
                    analysis.strength,

                confidence =
                    analysis.confidence,

                generatedAt =
                    now,

                sourcePrice =
                    analysis.snapshot
                        .currentPrice,

                timeframeSeconds =
                    seconds,

                expiresAt =
                    now.plus(
                        Duration.ofSeconds(
                            seconds
                        )
                    ),

                reasons =
                    analysis.reasons
            )

        return SignalState.Ready(
            signal =
                signal,

            analysis =
                analysis.snapshot
        )
    }
}
