package com.farhad.signalbot.core.model

/**
 * Supported market symbol.
 *
 * The provider-specific symbol is kept separate from the display name so
 * different market-data APIs can be supported later without changing UI code.
 */
data class TradingSymbol(
    val id: String,
    val displayName: String,
    val providerSymbol: String,
    val quoteCurrency: String
) {
    init {
        require(id.isNotBlank()) { "Symbol id cannot be blank" }
        require(displayName.isNotBlank()) { "Display name cannot be blank" }
        require(providerSymbol.isNotBlank()) { "Provider symbol cannot be blank" }
        require(quoteCurrency.isNotBlank()) { "Quote currency cannot be blank" }
    }
}
