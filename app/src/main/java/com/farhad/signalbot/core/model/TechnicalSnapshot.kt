package com.farhad.signalbot.core.model

data class TechnicalSnapshot(
    val currentPrice: Double,
    val emaFast: Double?,
    val emaSlow: Double?,
    val rsi: Double?,
    val macd: Double?,
    val macdSignal: Double?,
    val bullishScore: Double,
    val bearishScore: Double
) {
    init {
        require(currentPrice > 0.0 && currentPrice.isFinite())
        require(bullishScore in 0.0..100.0)
        require(bearishScore in 0.0..100.0)
    }
}
