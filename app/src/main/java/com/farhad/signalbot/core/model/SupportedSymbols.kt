package com.farhad.signalbot.core.model

object SupportedSymbols {

    val all: List<TradingSymbol> = listOf(
        TradingSymbol(
            id = "EURUSD",
            displayName = "EUR/USD",
            providerSymbol = "C:EURUSD",
            quoteCurrency = "USD"
        ),
        TradingSymbol(
            id = "GBPUSD",
            displayName = "GBP/USD",
            providerSymbol = "C:GBPUSD",
            quoteCurrency = "USD"
        ),
        TradingSymbol(
            id = "USDJPY",
            displayName = "USD/JPY",
            providerSymbol = "C:USDJPY",
            quoteCurrency = "JPY"
        ),
        TradingSymbol(
            id = "AUDUSD",
            displayName = "AUD/USD",
            providerSymbol = "C:AUDUSD",
            quoteCurrency = "USD"
        ),
        TradingSymbol(
            id = "USDCAD",
            displayName = "USD/CAD",
            providerSymbol = "C:USDCAD",
            quoteCurrency = "CAD"
        ),
        TradingSymbol(
            id = "USDCHF",
            displayName = "USD/CHF",
            providerSymbol = "C:USDCHF",
            quoteCurrency = "CHF"
        )
    )
}
