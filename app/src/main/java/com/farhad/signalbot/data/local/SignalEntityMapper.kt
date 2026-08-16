package com.farhad.signalbot.data.local

import com.farhad.signalbot.core.model.SignalOutcome
import com.farhad.signalbot.core.model.SignalRecord
import com.farhad.signalbot.core.model.SignalStrength
import com.farhad.signalbot.core.model.TradingSignal
import com.farhad.signalbot.core.model.TradingSymbol
import java.time.Instant

object SignalEntityMapper {

    fun toEntity(record: SignalRecord): SignalEntity {
        val signal = record.signal

        return SignalEntity(
            id = signal.id,
            symbolId = signal.symbol.id,
            symbolName = signal.symbol.displayName,
            providerSymbol = signal.symbol.providerSymbol,
            direction = signal.direction.name,
            strength = signal.strength.name,
            confidence = signal.confidence,
            generatedAtMillis =
                signal.generatedAt.toEpochMilli(),
            sourcePrice = signal.sourcePrice,
            timeframeSeconds = signal.timeframeSeconds,
            expiresAtMillis =
                signal.expiresAt.toEpochMilli(),
            outcome = record.outcome.name,
            evaluatedAtMillis =
                record.evaluatedAt?.toEpochMilli(),
            resultPrice = record.resultPrice,
            reasons = signal.reasons.joinToString(REASON_SEPARATOR)
        )
    }

    fun fromEntity(entity: SignalEntity): SignalRecord {

        val signal = TradingSignal(
            id = entity.id,
            symbol = TradingSymbol(
                id = entity.symbolId,
                displayName = entity.symbolName,
                providerSymbol = entity.providerSymbol,
                quoteCurrency = "USD"
            ),
            direction =
                com.farhad.signalbot.core.model.SignalDirection
                    .valueOf(entity.direction),
            strength =
                SignalStrength.valueOf(entity.strength),
            confidence = entity.confidence,
            generatedAt =
                Instant.ofEpochMilli(entity.generatedAtMillis),
            sourcePrice = entity.sourcePrice,
            timeframeSeconds = entity.timeframeSeconds,
            expiresAt =
                Instant.ofEpochMilli(entity.expiresAtMillis),
            reasons =
                entity.reasons
                    .split(REASON_SEPARATOR)
                    .filter(String::isNotBlank)
        )

        return SignalRecord(
            signal = signal,
            outcome = SignalOutcome.valueOf(entity.outcome),
            evaluatedAt =
                entity.evaluatedAtMillis?.let(
                    Instant::ofEpochMilli
                ),
            resultPrice = entity.resultPrice
        )
    }

    private const val REASON_SEPARATOR = "|||"
}
