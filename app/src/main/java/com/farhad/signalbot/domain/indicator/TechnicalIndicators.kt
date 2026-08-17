package com.farhad.signalbot.domain.indicator

import kotlin.math.abs
import kotlin.math.max

object TechnicalIndicators {

    fun sma(
        values: List<Double>,
        period: Int
    ): Double? {

        if (
            period <= 0 ||
            values.size < period
        ) return null

        return values
            .takeLast(period)
            .average()
    }

    fun ema(
        values: List<Double>,
        period: Int
    ): Double? {

        if (
            period <= 0 ||
            values.size < period
        ) return null

        val multiplier =
            2.0 / (period + 1)

        var result =
            values
                .take(period)
                .average()

        for (
            i in period until values.size
        ) {
            result =
                (
                    (values[i] - result) *
                        multiplier
                    ) + result
        }

        return result
    }

    fun rsi(
        values: List<Double>,
        period: Int = 14
    ): Double? {

        if (
            period <= 0 ||
            values.size <= period
        ) return null

        var gains = 0.0
        var losses = 0.0

        for (i in 1..period) {

            val change =
                values[i] -
                    values[i - 1]

            if (change >= 0) {
                gains += change
            } else {
                losses += abs(change)
            }
        }

        var avgGain =
            gains / period

        var avgLoss =
            losses / period

        for (
            i in period + 1 until values.size
        ) {

            val change =
                values[i] -
                    values[i - 1]

            val gain =
                max(change, 0.0)

            val loss =
                max(-change, 0.0)

            avgGain =
                (
                    avgGain *
                        (period - 1) +
                        gain
                    ) / period

            avgLoss =
                (
                    avgLoss *
                        (period - 1) +
                        loss
                    ) / period
        }

        if (avgLoss == 0.0) {
            return 100.0
        }

        val rs =
            avgGain / avgLoss

        return 100.0 -
            (100.0 / (1.0 + rs))
    }

    fun atr(
        candles: List<com.farhad.signalbot.core.model.MarketCandle>,
        period: Int = 14
    ): Double? {

        if (
            candles.size <= period
        ) return null

        val trueRanges =
            mutableListOf<Double>()

        for (i in 1 until candles.size) {

            val current =
                candles[i]

            val previous =
                candles[i - 1]

            val tr =
                max(
                    current.high -
                        current.low,

                    max(
                        abs(
                            current.high -
                                previous.close
                        ),

                        abs(
                            current.low -
                                previous.close
                        )
                    )
                )

            trueRanges += tr
        }

        return sma(
            trueRanges,
            period
        )
    }

    fun macd(
        values: List<Double>,
        fastPeriod: Int = 12,
        slowPeriod: Int = 26
    ): Double? {

        val fast =
            ema(
                values,
                fastPeriod
            )
                ?: return null

        val slow =
            ema(
                values,
                slowPeriod
            )
                ?: return null

        return fast - slow
    }

    fun macdSignal(
        values: List<Double>,
        fastPeriod: Int = 12,
        slowPeriod: Int = 26,
        signalPeriod: Int = 9
    ): Double? {

        if (
            values.size <
            slowPeriod +
            signalPeriod
        ) return null

        val macdValues =
            mutableListOf<Double>()

        for (
            end in slowPeriod..values.size
        ) {

            val slice =
                values.take(end)

            val fast =
                ema(
                    slice,
                    fastPeriod
                )

            val slow =
                ema(
                    slice,
                    slowPeriod
                )

            if (
                fast != null &&
                slow != null
            ) {
                macdValues +=
                    fast - slow
            }
        }

        return ema(
            macdValues,
            signalPeriod
        )
    }
}
