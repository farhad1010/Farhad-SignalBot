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
    private val trendAnalyzer:
        TrendAnalyzer =
        TrendAnalyzer()
) {

    fun analyze(
        candles: List<MarketCandle>
    ): Result<SignalAnalysisResult> {

        if (
            candles.size <
            MINIMUM_CANDLES
        ) {
            return Result.failure(
                IllegalArgumentException(
                    "At least $MINIMUM_CANDLES candles are required."
                )
            )
        }

        val ordered =
            candles.sortedBy {
                it.openTime
            }

        val closes =
            ordered.map {
                it.close
            }

        val current =
            ordered.last()

        val previous =
            ordered[ordered.lastIndex - 1]

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
                ordered,
                14
            )

        val trend =
            trendAnalyzer.analyze(
                ordered
            )

        var bullish = 0.0
        var bearish = 0.0

        val reasons =
            mutableListOf<String>()

        /*
         * EMA alignment
         */
        if (
            ema9 != null &&
            ema21 != null &&
            ema50 != null
        ) {

            if (
                ema9 >
                ema21 &&
                ema21 >
                ema50
            ) {

                bullish += 25.0

                reasons +=
                    "EMA 9/21/50 bullish alignment."

            } else if (
                ema9 <
                ema21 &&
                ema21 <
                ema50
            ) {

                bearish += 25.0

                reasons +=
                    "EMA 9/21/50 bearish alignment."

            } else {

                bullish +=
                    if (ema9 > ema21)
                        8.0
                    else 0.0

                bearish +=
                    if (ema9 < ema21)
                        8.0
                    else 0.0
            }
        }

        /*
         * RSI momentum
         */
        if (rsi != null) {

            when {

                rsi in 52.0..68.0 -> {

                    bullish += 18.0

                    reasons +=
                        "RSI confirms bullish momentum."
                }

                rsi in 32.0..48.0 -> {

                    bearish += 18.0

                    reasons +=
                        "RSI confirms bearish momentum."
                }

                rsi > 72.0 -> {

                    bullish += 4.0

                    bearish += 8.0

                    reasons +=
                        "RSI is strongly overbought."
                }

                rsi < 28.0 -> {

                    bullish += 8.0

                    bearish += 4.0

                    reasons +=
                        "RSI is strongly oversold."
                }
            }
        }

        /*
         * MACD
         */
        if (
            macd != null &&
            macdSignal != null
        ) {

            when {

                macd >
                    macdSignal -> {

                    bullish += 18.0

                    reasons +=
                        "MACD is above signal."
                }

                macd <
                    macdSignal -> {

                    bearish += 18.0

                    reasons +=
                        "MACD is below signal."
                }
            }
        }

        /*
         * Candle momentum
         */
        val body =
            current.bodySize

        val range =
            current.range

        if (
            range > 0.0 &&
            body / range >= 0.55
        ) {

            if (current.isBullish) {

                bullish += 10.0

                reasons +=
                    "Strong bullish candle body."

            } else if (
                current.isBearish
            ) {

                bearish += 10.0

                reasons +=
                    "Strong bearish candle body."
            }
        }

        /*
         * Immediate momentum
         */
        if (
            current.close >
            previous.close
        ) {

            bullish += 5.0

        } else if (
            current.close <
            previous.close
        ) {

            bearish += 5.0
        }

        /*
         * Trend confirmation
         */
        when (trend) {

            MarketTrend.STRONG_BULLISH -> {

                bullish += 20.0

                reasons +=
                    "Higher trend structure is bullish."
            }

            MarketTrend.BULLISH -> {

                bullish += 10.0

                reasons +=
                    "Market trend is bullish."
            }

            MarketTrend.STRONG_BEARISH -> {

                bearish += 20.0

                reasons +=
                    "Higher trend structure is bearish."
            }

            MarketTrend.BEARISH -> {

                bearish += 10.0

                reasons +=
                    "Market trend is bearish."
            }

            MarketTrend.NEUTRAL -> {

                reasons +=
                    "Trend confirmation is neutral."
            }
        }

        /*
         * Avoid trading when the two sides
         * are too close.
         */
        val difference =
            abs(
                bullish -
                    bearish
            )

        val direction =
            when {

                bullish >=
                    MINIMUM_DIRECTION_SCORE &&
                    difference >=
                    MINIMUM_EDGE ->
                    SignalDirection.CALL

                bearish >=
                    MINIMUM_DIRECTION_SCORE &&
                    difference >=
                    MINIMUM_EDGE ->
                    SignalDirection.PUT

                else ->
                    SignalDirection.NEUTRAL
            }

        val winningScore =
            when (direction) {

                SignalDirection.CALL ->
                    bullish

                SignalDirection.PUT ->
                    bearish

                SignalDirection.NEUTRAL ->
                    0.0
            }

        /*
         * This is a model strength score,
         * NOT a guaranteed probability of winning.
         */
        val confidence =
            if (
                direction ==
                    SignalDirection.NEUTRAL
            ) {
                0.0
            } else {

                val base =
                    winningScore
                        .coerceIn(
                            0.0,
                            100.0
                        )

                /*
                 * Require a meaningful edge.
                 * Do not display fake 90–99%
                 * values just because indicators
                 * happen to agree.
                 */
                base
                    .coerceIn(
                        55.0,
                        88.0
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

        /*
         * Adaptive analysis window.
         *
         * This is a strategy-derived holding
         * window, NOT a promise that price will
         * move in that direction for that many
         * seconds.
         */
        val recommendedSeconds =
            when {

                atr == null ->
                    60L

                range <= 0.0 ->
                    60L

                atr / current.close >=
                    0.0015 ->
                    60L

                atr / current.close >=
                    0.0008 ->
                    90L

                else ->
                    120L
            }

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
                    bullish.coerceIn(
                        0.0,
                        100.0
                    ),

                bearishScore =
                    bearish.coerceIn(
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

    private companion object {

        const val MINIMUM_CANDLES = 80

        const val MINIMUM_DIRECTION_SCORE =
            58.0

        const val MINIMUM_EDGE =
            12.0
    }
}
