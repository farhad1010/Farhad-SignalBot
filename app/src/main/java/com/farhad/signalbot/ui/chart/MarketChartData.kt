package com.farhad.signalbot.ui.chart

import com.farhad.signalbot.core.model.MarketCandle

data class MarketChartData(
    val points: List<ChartPoint>,
    val minPrice: Double,
    val maxPrice: Double
) {
    companion object {

        fun from(
            candles: List<MarketCandle>
        ): MarketChartData {

            require(candles.isNotEmpty()) {
                "Cannot build chart from empty candles."
            }

            val closes = candles.map { it.close }

            val emaFast =
                emaSeries(closes, 12)

            val emaSlow =
                emaSeries(closes, 26)

            val points = candles.mapIndexed { index, candle ->

                ChartPoint(
                    timestampMillis =
                        candle.openTime.toEpochMilli(),

                    open = candle.open,
                    high = candle.high,
                    low = candle.low,
                    close = candle.close,
                    volume = candle.volume,

                    emaFast = emaFast[index],
                    emaSlow = emaSlow[index]
                )
            }

            val minPrice =
                candles.minOf { it.low }

            val maxPrice =
                candles.maxOf { it.high }

            return MarketChartData(
                points = points,
                minPrice = minPrice,
                maxPrice = maxPrice
            )
        }

        private fun emaSeries(
            values: List<Double>,
            period: Int
        ): List<Double?> {

            if (values.isEmpty()) {
                return emptyList()
            }

            val result =
                MutableList<Double?>(
                    values.size
                ) { null }

            if (values.size < period) {
                return result
            }

            val multiplier =
                2.0 / (period + 1)

            var ema =
                values.take(period).average()

            result[period - 1] = ema

            for (index in period until values.size) {

                ema =
                    ((values[index] - ema) * multiplier) +
                    ema

                result[index] = ema
            }

            return result
        }
    }
}
