package com.farhad.signalbot.domain

import com.farhad.signalbot.core.model.MarketCandle
import com.farhad.signalbot.core.model.SignalDirection
import com.farhad.signalbot.core.model.SignalStrength
import com.farhad.signalbot.core.model.TechnicalSnapshot
import com.farhad.signalbot.domain.indicator.MarketTrend
import com.farhad.signalbot.domain.indicator.TechnicalIndicators
import com.farhad.signalbot.domain.indicator.TrendAnalyzer

class SignalEngine(
    private val trendAnalyzer: TrendAnalyzer = TrendAnalyzer()
) {

    fun analyze(
        candles: List<MarketCandle>
    ): Result<SignalAnalysisResult> {

        if (candles.size < MINIMUM_CANDLES) {
            return Result.failure(
                IllegalArgumentException(
                    "At least $MINIMUM_CANDLES candles are required."
                )
            )
        }

        val closes = candles.map { it.close }
        val currentPrice = closes.last()

        val emaFast =
            TechnicalIndicators.ema(closes, FAST_EMA)

        val emaSlow =
            TechnicalIndicators.ema(closes, SLOW_EMA)

        val rsi =
            TechnicalIndicators.rsi(closes, RSI_PERIOD)

        val macd =
            TechnicalIndicators.macd(
                closes,
                FAST_EMA,
                SLOW_EMA
            )

        val macdSignal =
            TechnicalIndicators.macdSignal(
                closes,
                FAST_EMA,
                SLOW_EMA,
                MACD_SIGNAL
            )

        val trend = trendAnalyzer.analyze(candles)

        var bullishScore = 0.0
        var bearishScore = 0.0

        val reasons = mutableListOf<String>()

        if (emaFast != null && emaSlow != null) {
            if (emaFast > emaSlow) {
                bullishScore += 25
                reasons += "Fast EMA is above slow EMA."
            } else if (emaFast < emaSlow) {
                bearishScore += 25
                reasons += "Fast EMA is below slow EMA."
            }
        }

        if (rsi != null) {
            when {
                rsi >= 55.0 && rsi < 70.0 -> {
                    bullishScore += 20
                    reasons += "RSI supports bullish momentum."
                }

                rsi <= 45.0 && rsi > 30.0 -> {
                    bearishScore += 20
                    reasons += "RSI supports bearish momentum."
                }

                rsi >= 70.0 -> {
                    bearishScore += 10
                    reasons += "RSI indicates an overbought condition."
                }

                rsi <= 30.0 -> {
                    bullishScore += 10
                    reasons += "RSI indicates an oversold condition."
                }
            }
        }

        if (macd != null && macdSignal != null) {
            when {
                macd > macdSignal -> {
                    bullishScore += 25
                    reasons += "MACD is above its signal line."
                }

                macd < macdSignal -> {
                    bearishScore += 25
                    reasons += "MACD is below its signal line."
                }
            }
        }

        when (trend) {
            MarketTrend.STRONG_BULLISH -> {
                bullishScore += 30
                reasons += "Overall market trend is strongly bullish."
            }

            MarketTrend.BULLISH -> {
                bullishScore += 15
                reasons += "Overall market trend is bullish."
            }

            MarketTrend.STRONG_BEARISH -> {
                bearishScore += 30
                reasons += "Overall market trend is strongly bearish."
            }

            MarketTrend.BEARISH -> {
                bearishScore += 15
                reasons += "Overall market trend is bearish."
            }

            MarketTrend.NEUTRAL -> {
                reasons += "Market trend is currently neutral."
            }
        }

        val direction =
            when {
                bullishScore > bearishScore &&
                    bullishScore >= MINIMUM_SIGNAL_SCORE ->
                    SignalDirection.CALL

                bearishScore > bullishScore &&
                    bearishScore >= MINIMUM_SIGNAL_SCORE ->
                    SignalDirection.PUT

                else ->
                    SignalDirection.NEUTRAL
            }

        val confidence =
            when (direction) {
                SignalDirection.CALL ->
                    bullishScore.coerceIn(0.0, 100.0)

                SignalDirection.PUT ->
                    bearishScore.coerceIn(0.0, 100.0)

                SignalDirection.NEUTRAL ->
                    0.0
            }

        val strength =
            when {
                confidence >= 85.0 ->
                    SignalStrength.VERY_STRONG

                confidence >= 70.0 ->
                    SignalStrength.STRONG

                confidence >= 55.0 ->
                    SignalStrength.MODERATE

                else ->
                    SignalStrength.WEAK
            }

        val snapshot = TechnicalSnapshot(
            currentPrice = currentPrice,
            emaFast = emaFast,
            emaSlow = emaSlow,
            rsi = rsi,
            macd = macd,
            macdSignal = macdSignal,
            bullishScore = bullishScore.coerceIn(0.0, 100.0),
            bearishScore = bearishScore.coerceIn(0.0, 100.0)
        )

        return Result.success(
            SignalAnalysisResult(
                direction = direction,
                strength = strength,
                confidence = confidence,
                snapshot = snapshot,
                trend = trend,
                reasons = reasons.distinct()
            )
        )
    }

    private companion object {
        const val MINIMUM_CANDLES = 50

        const val FAST_EMA = 12
        const val SLOW_EMA = 26

        const val RSI_PERIOD = 14
        const val MACD_SIGNAL = 9

        const val MINIMUM_SIGNAL_SCORE = 55.0
    }
}
