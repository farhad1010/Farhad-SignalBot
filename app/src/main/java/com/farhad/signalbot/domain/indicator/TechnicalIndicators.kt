package com.farhad.signalbot.domain.indicator

import kotlin.math.abs

object TechnicalIndicators {

    fun sma(values: List<Double>, period: Int): Double? {
        if (period <= 0 || values.size < period) return null

        return values
            .takeLast(period)
            .average()
    }

    fun ema(values: List<Double>, period: Int): Double? {
        if (period <= 0 || values.size < period) return null

        val multiplier = 2.0 / (period + 1)

        var ema = values
            .take(period)
            .average()

        for (index in period until values.size) {
            ema = ((values[index] - ema) * multiplier) + ema
        }

        return ema
    }

    fun rsi(values: List<Double>, period: Int = 14): Double? {
        if (period <= 0 || values.size <= period) return null

        var gains = 0.0
        var losses = 0.0

        for (i in 1..period) {
            val change = values[i] - values[i - 1]

            if (change > 0) {
                gains += change
            } else {
                losses += abs(change)
            }
        }

        var averageGain = gains / period
        var averageLoss = losses / period

        for (i in (period + 1) until values.size) {
            val change = values[i] - values[i - 1]

            val gain = if (change > 0) change else 0.0
            val loss = if (change < 0) abs(change) else 0.0

            averageGain =
                ((averageGain * (period - 1)) + gain) / period

            averageLoss =
                ((averageLoss * (period - 1)) + loss) / period
        }

        if (averageLoss == 0.0) return 100.0

        val relativeStrength = averageGain / averageLoss

        return 100.0 - (100.0 / (1.0 + relativeStrength))
    }

    fun macd(
        values: List<Double>,
        fastPeriod: Int = 12,
        slowPeriod: Int = 26
    ): Double? {

        val fast = ema(values, fastPeriod) ?: return null
        val slow = ema(values, slowPeriod) ?: return null

        return fast - slow
    }

    fun macdSignal(
        values: List<Double>,
        fastPeriod: Int = 12,
        slowPeriod: Int = 26,
        signalPeriod: Int = 9
    ): Double? {

        if (values.size < slowPeriod + signalPeriod) return null

        val macdValues = mutableListOf<Double>()

        for (end in slowPeriod..values.size) {
            val slice = values.take(end)

            val fast = ema(slice, fastPeriod)
            val slow = ema(slice, slowPeriod)

            if (fast != null && slow != null) {
                macdValues += fast - slow
            }
        }

        return ema(macdValues, signalPeriod)
    }
}
