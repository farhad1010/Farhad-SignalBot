package com.farhad.signalbot.ui.chart

data class ChartPoint(
    val timestampMillis: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double,
    val emaFast: Double?,
    val emaSlow: Double?
)
