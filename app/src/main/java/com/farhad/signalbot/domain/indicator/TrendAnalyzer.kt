package com.farhad.signalbot.domain.indicator

import com.farhad.signalbot.core.model.MarketCandle

enum class MarketTrend {
    STRONG_BULLISH,
    BULLISH,
    NEUTRAL,
    BEARISH,
    STRONG_BEARISH
}

class TrendAnalyzer {

    fun analyze(candles: List<MarketCandle>): MarketTrend {
        if (candles.size < 50) {
            return MarketTrend.NEUTRAL
        }

        val closes = candles.map { it.close }

        val fastEma =
            TechnicalIndicators.ema(closes, 12)
                ?: return MarketTrend.NEUTRAL

        val slowEma =
            TechnicalIndicators.ema(closes, 26)
                ?: return MarketTrend.NEUTRAL

        val currentPrice = closes.last()

        val bullishDistance =
            ((currentPrice - slowEma) / slowEma) * 100.0

        val bearishDistance =
            ((slowEma - currentPrice) / slowEma) * 100.0

        return when {
            fastEma > slowEma && bullishDistance >= 0.50 ->
                MarketTrend.STRONG_BULLISH

            fastEma > slowEma ->
                MarketTrend.BULLISH

            fastEma < slowEma && bearishDistance >= 0.50 ->
                MarketTrend.STRONG_BEARISH

            fastEma < slowEma ->
                MarketTrend.BEARISH

            else ->
                MarketTrend.NEUTRAL
        }
    }
}
