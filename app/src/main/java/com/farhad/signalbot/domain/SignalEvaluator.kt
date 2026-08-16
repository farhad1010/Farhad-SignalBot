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

        if (record.outcome != SignalOutcome.PENDING) {
            return record
        }

        val signal = record.signal

        val expiryCandle =
            candles.lastOrNull {
                !it.closeTime.isBefore(signal.expiresAt)
            }

        if (expiryCandle == null) {
            return record
        }

        val entry = signal.sourcePrice
        val result = expiryCandle.close

        val outcome =
            when (signal.direction) {

                SignalDirection.CALL ->
                    if (result > entry) {
                        SignalOutcome.WIN
                    } else {
                        SignalOutcome.LOSS
                    }

                SignalDirection.PUT ->
                    if (result < entry) {
                        SignalOutcome.WIN
                    } else {
                        SignalOutcome.LOSS
                    }

                SignalDirection.NEUTRAL ->
                    SignalOutcome.CANCELLED
            }

        return record.copy(
            outcome = outcome,
            evaluatedAt = now,
            resultPrice = result
        )
    }
}
