package com.farhad.signalbot.domain

import com.farhad.signalbot.core.model.SignalDirection
import com.farhad.signalbot.core.model.SignalStrength
import com.farhad.signalbot.core.model.TechnicalSnapshot
import com.farhad.signalbot.domain.indicator.MarketTrend

data class SignalAnalysisResult(
    val direction: SignalDirection,
    val strength: SignalStrength,
    val confidence: Double,
    val snapshot: TechnicalSnapshot,
    val trend: MarketTrend,
    val reasons: List<String>
)
