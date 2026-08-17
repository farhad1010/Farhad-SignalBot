package com.farhad.signalbot.ui.state

import com.farhad.signalbot.core.model.MarketCandle
import com.farhad.signalbot.core.model.SignalState
import com.farhad.signalbot.core.model.SupportedSymbols
import com.farhad.signalbot.core.model.TradingSymbol

data class SignalDashboardState(
    val selectedSymbol: TradingSymbol =
        SupportedSymbols.all.first(),

    val candles: List<MarketCandle> =
        emptyList(),

    val currentPrice: Double? =
        null,

    val previousPrice: Double? =
        null,

    val priceChangePercent: Double? =
        null,

    val signalState: SignalState =
        SignalState.Idle,

    val isRefreshing: Boolean =
        false,

    val lastUpdatedMillis: Long? =
        null,

    val errorMessage: String? =
        null
)
