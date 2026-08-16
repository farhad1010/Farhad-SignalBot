package com.farhad.signalbot.ui.state

import com.farhad.signalbot.core.model.MarketCandle
import com.farhad.signalbot.core.model.SignalState
import com.farhad.signalbot.core.model.TradingSymbol

data class SignalDashboardState(
    val selectedSymbol: TradingSymbol = DEFAULT_SYMBOL,
    val candles: List<MarketCandle> = emptyList(),
    val currentPrice: Double? = null,
    val previousPrice: Double? = null,
    val priceChangePercent: Double? = null,
    val signalState: SignalState = com.farhad.signalbot.core.model.SignalState.Idle,
    val isRefreshing: Boolean = false,
    val lastUpdatedMillis: Long? = null,
    val errorMessage: String? = null
) {
    companion object {
        val DEFAULT_SYMBOL = TradingSymbol(
            id = "AAPL",
            displayName = "Apple",
            providerSymbol = "AAPL",
            quoteCurrency = "USD"
        )
    }
}
