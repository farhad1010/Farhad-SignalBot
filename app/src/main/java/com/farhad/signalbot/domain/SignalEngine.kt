package com.farhad.signalbot.domain

import com.farhad.signalbot.core.model.MarketCandle
import com.farhad.signalbot.core.model.SignalDirection
import com.farhad.signalbot.core.model.SignalStrength
import com.farhad.signalbot.core.model.TechnicalSnapshot
import com.farhad.signalbot.domain.indicator.MarketTrend
import com.farhad.signalbot.domain.indicator.TechnicalIndicators
import com.farhad.signalbot.domain.indicator.TrendAnalyzer
import kotlin.math.abs

class SignalEngine(
    private val trendAnalyzer: TrendAnalyzer =
        TrendAnalyzer()
) {

    fun analyze(
        candles: List<MarketCandle>
    ): Result<SignalAnalysisResult> {

        if (candles.size < MINIMUM_CANDLES) {
            return Result.failure(
                IllegalArgumentException(
                    "Not enough live market data."
                )
            )
        }

        val data =
            candles
                .sortedBy { it.openTime }
                .takeLast(MAX_ANALYSIS_CANDLES)

        val closes =
            data.map { it.close }

        val current = data.last()

        val previous =
            data[data.lastIndex - 1]

        val ema9 =
            TechnicalIndicators.ema(
                closes,
                9
            )

        val ema21 =
            TechnicalIndicators.ema(
                closes,
                21
            )

        val ema50 =
            TechnicalIndicators.ema(
                closes,
                50
            )

        val rsi =
            TechnicalIndicators.rsi(
                closes,
                14
            )

        val macd =
            TechnicalIndicators.macd(
                closes
            )

        val macdSignal =
            TechnicalIndicators.macdSignal(
                closes
            )

        val atr =
            TechnicalIndicators.atr(
                data,
                14
            )

        val trend =
            trendAnalyzer.analyze(data)

        var bull = 0.0
        var bear = 0.0

        val reasons =
            mutableListOf<String>()

        // EMA structure
        if (
            ema9 != null &&
            ema21 != null &&
            ema50 != null
        ) {

            when {

                ema9 > ema21 &&
                    ema21 > ema50 -> {

                    bull += 25.0

                    reasons +=
                        "EMA structure is bullish."
                }

                ema9 < ema21 &&
                    ema21 < ema50 -> {

                    bear += 25.0

                    reasons +=
                        "EMA structure is bearish."
                }

                ema9 > ema21 -> {
                    bull += 10.0
                }

                ema9 < ema21 -> {
                    bear += 10.0
                }
            }
        }

        // RSI
        rsi?.let {

            when {

                it in 52.0..68.0 -> {

                    bull += 18.0

                    reasons +=
                        "RSI confirms bullish momentum."
                }

                it in 32.0..48.0 -> {

                    bear += 18.0

                    reasons +=
                        "RSI confirms bearish momentum."
                }

                it > 72.0 -> {

                    bear += 8.0

                    reasons +=
                        "RSI is overbought."
                }

                it < 28.0 -> {

                    bull += 8.0

                    reasons +=
                        "RSI is oversold."
                }
            }
        }

        // MACD
        if (
            macd != null &&
            macdSignal != null
        ) {

            when {

                macd > macdSignal -> {

                    bull += 18.0

                    reasons +=
                        "MACD is bullish."
                }

                macd < macdSignal -> {

                    bear += 18.0

                    reasons +=
                        "MACD is bearish."
                }
            }
        }

        // Candle momentum
        if (current.range > 0.0) {

            val bodyRatio =
                current.bodySize /
                    current.range

            if (bodyRatio >= 0.55) {

                when {

                    current.isBullish -> {

                        bull += 10.0

                        reasons +=
                            "Bullish candle momentum."
                    }

                    current.isBearish -> {

                        bear += 10.0

                        reasons +=
                            "Bearish candle momentum."
                    }
                }
            }
        }

        // Immediate momentum
        when {

            current.close > previous.close ->
                bull += 5.0

            current.close < previous.close ->
                bear += 5.0
        }

        // Trend
        when (trend) {

            MarketTrend.STRONG_BULLISH -> {

                bull += 20.0

                reasons +=
                    "Market trend is strongly bullish."
            }

            MarketTrend.BULLISH -> {

                bull += 10.0

                reasons +=
                    "Market trend is bullish."
            }

            MarketTrend.STRONG_BEARISH -> {

                bear += 20.0

                reasons +=
                    "Market trend is strongly bearish."
            }

            MarketTrend.BEARISH -> {

                bear += 10.0

                reasons +=
                    "Market trend is bearish."
            }

            MarketTrend.NEUTRAL -> {

                reasons +=
                    "Market trend is neutral."
            }
        }

        val edge =
            abs(bull - bear)

        val direction =
            when {

                bull >= 60.0 &&
                    edge >= 15.0 ->
                    SignalDirection.CALL

                bear >= 60.0 &&
                    edge >= 15.0 ->
                    SignalDirection.PUT

                else ->
                    SignalDirection.NEUTRAL
            }

        val score =
            when (direction) {

                SignalDirection.CALL ->
                    bull

                SignalDirection.PUT ->
                    bear

                SignalDirection.NEUTRAL ->
                    0.0
            }

        /*
         * This is MODEL CONFIDENCE.
         *
         * It is NOT a guaranteed probability
         * of winning.
         *
         * Historical calibration will later
         * replace this model score with
         * empirically observed performance.
         */

        val confidence =
            if (
                direction ==
                    SignalDirection.NEUTRAL
            ) {
                0.0
            } else {

                (
                    50.0 +
                        edge * 1.6
                    )
                    .coerceIn(
                        50.0,
                        85.0
                    )
            }

        val strength =
            when {

                confidence >= 80.0 ->
                    SignalStrength.VERY_STRONG

                confidence >= 70.0 ->
                    SignalStrength.STRONG

                confidence >= 60.0 ->
                    SignalStrength.MODERATE

                else ->
                    SignalStrength.WEAK
            }

        val recommendedSeconds =
            chooseWindow(
                confidence = confidence,
                atr = atr,
                price = current.close
            )

        val snapshot =
            TechnicalSnapshot(
                currentPrice =
                    current.close,

                emaFast =
                    ema9,

                emaSlow =
                    ema21,

                rsi =
                    rsi,

                macd =
                    macd,

                macdSignal =
                    macdSignal,

                bullishScore =
                    bull.coerceIn(
                        0.0,
                        100.0
                    ),

                bearishScore =
                    bear.coerceIn(
                        0.0,
                        100.0
                    )
            )

        return Result.success(
            SignalAnalysisResult(

                direction =
                    direction,

                strength =
                    strength,

                confidence =
                    confidence,

                recommendedSeconds =
                    recommendedSeconds,

                snapshot =
                    snapshot,

                trend =
                    trend,

                reasons =
                    reasons.distinct()
            )
        )
    }

    private fun chooseWindow(
        confidence: Double,
        atr: Double?,
        price: Double
    ): Long {

        if (confidence < 60.0) {
            return 10L
        }

        if (
            atr == null ||
            price <= 0.0
        ) {
            return 30L
        }

        val volatility =
            atr / price

        return when {

            volatility >= 0.0015 &&
                confidence >= 75.0 ->
                10L

            volatility >= 0.0010 &&
                confidence >= 70.0 ->
                20L

            volatility >= 0.0006 ->
                30L

            confidence >= 80.0 ->
                45L

            else ->
                60L
        }
    }

    private companion object {

        const val MINIMUM_CANDLES = 80

        const val MAX_ANALYSIS_CANDLES = 300
    }
}
