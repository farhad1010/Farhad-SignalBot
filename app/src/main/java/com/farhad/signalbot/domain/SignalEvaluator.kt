package com.farhad.signalbot.domain

import com.farhad.signalbot.core.model.MarketCandle
import com.farhad.signalbot.core.model.SignalDirection
import com.farhad.signalbot.core.model.SignalOutcome
import com.farhad.signalbot.core.model.SignalRecord
import java.time.Instant

class SignalEvaluator {

    fun evaluate(
        record: SignalRecord,
        candles: List<MarketCandle>,
        now: Instant = Instant.now()
    ): SignalRecord {

        if (
            record.outcome !=
            SignalOutcome.PENDING
        ) {
            return record
        }

        val signal =
            record.signal

        val expiry =
            signal.expiresAt

        /*
         * We need market data that actually
         * reaches the requested expiry.
         */
        val future =
            candles
                .sortedBy {
                    it.closeTime
                }
                .firstOrNull {
                    !it.closeTime
                        .isBefore(
                            expiry
                        )
                }

        if (
            future == null
        ) {
            return record
        }

        val entry =
            signal.sourcePrice

        val exit =
            future.close

        val outcome =
            when (
                signal.direction
            ) {

                SignalDirection.CALL ->

                    when {

                        exit > entry ->
                            SignalOutcome.WIN

                        exit < entry ->
                            SignalOutcome.LOSS

                        else ->
                            SignalOutcome.CANCELLED
                    }

                SignalDirection.PUT ->

                    when {

                        exit < entry ->
                            SignalOutcome.WIN

                        exit > entry ->
                            SignalOutcome.LOSS

                        else ->
                            SignalOutcome.CANCELLED
                    }

                SignalDirection.NEUTRAL ->
                    SignalOutcome.CANCELLED
            }

        return record.copy(
            outcome =
                outcome,

            evaluatedAt =
                now,

            resultPrice =
                exit
        )
    }
}
