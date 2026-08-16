package com.farhad.signalbot.core.model

import java.time.Instant

/**
 * Immutable OHLCV candle.
 *
 * All prices use Double because market-data providers commonly expose
 * decimal prices. Time is represented as Instant to avoid timezone bugs.
 */
data class MarketCandle(
    val openTime: Instant,
    val closeTime: Instant,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
) {
    init {
        require(open.isFinite()) { "Open price must be finite" }
        require(high.isFinite()) { "High price must be finite" }
        require(low.isFinite()) { "Low price must be finite" }
        require(close.isFinite()) { "Close price must be finite" }
        require(volume.isFinite() && volume >= 0.0) {
            "Volume must be finite and non-negative"
        }

        require(high >= open && high >= close && high >= low) {
            "Invalid candle high"
        }

        require(low <= open && low <= close && low <= high) {
            "Invalid candle low"
        }

        require(!closeTime.isBefore(openTime)) {
            "Close time cannot be before open time"
        }
    }

    val bodySize: Double
        get() = kotlin.math.abs(close - open)

    val range: Double
        get() = high - low

    val isBullish: Boolean
        get() = close > open

    val isBearish: Boolean
        get() = close < open

    val isDoji: Boolean
        get() = close == open
}
