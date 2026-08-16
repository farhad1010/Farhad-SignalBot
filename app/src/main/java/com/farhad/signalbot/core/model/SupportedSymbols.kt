package com.farhad.signalbot.core.model

object SupportedSymbols {

    val all: List<TradingSymbol> = listOf(

        TradingSymbol(
            id = "AAPL",
            displayName = "Apple",
            providerSymbol = "AAPL",
            quoteCurrency = "USD"
        ),

        TradingSymbol(
            id = "MSFT",
            displayName = "Microsoft",
            providerSymbol = "MSFT",
            quoteCurrency = "USD"
        ),

        TradingSymbol(
            id = "NVDA",
            displayName = "NVIDIA",
            providerSymbol = "NVDA",
            quoteCurrency = "USD"
        ),

        TradingSymbol(
            id = "TSLA",
            displayName = "Tesla",
            providerSymbol = "TSLA",
            quoteCurrency = "USD"
        ),

        TradingSymbol(
            id = "AMZN",
            displayName = "Amazon",
            providerSymbol = "AMZN",
            quoteCurrency = "USD"
        ),

        TradingSymbol(
            id = "GOOGL",
            displayName = "Alphabet",
            providerSymbol = "GOOGL",
            quoteCurrency = "USD"
        )
    )
}
