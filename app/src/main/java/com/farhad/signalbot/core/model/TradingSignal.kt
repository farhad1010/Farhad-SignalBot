package com.farhad.signalbot.core.model

import java.time.Instant

/**
 * A signal produced from actual market analysis.
 *
 * This model deliberately contains no random/fake signal generation.
 */
data class TradingSignal(
    val id: String,
    val symbol: TradingSymbol,
    val direction: SignalDirection,
    val strength: SignalStrength,
    val confidence: Double,
    val generatedAt: Instant,
    val sourcePrice: Double,
    val timeframeSeconds: Long,
    val expiresAt: Instant,
    val reasons: List<String>
) {
    init {
        require(id.isNotBlank()) { "Signal id cannot be blank" }
        require(confidence in 0.0..100.0) {
            "Confidence must be between 0 and 100"
        }
        require(sourcePrice.isFinite() && sourcePrice > 0.0) {
            "Source price must be positive and finite"
        }
        require(timeframeSeconds > 0) {
            "Timeframe must be greater than zero"
        }
        require(!expiresAt.isBefore(generatedAt)) {
            "Signal expiry cannot be before generation time"
        }
    }
}
