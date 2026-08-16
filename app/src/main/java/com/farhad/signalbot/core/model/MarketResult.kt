package com.farhad.signalbot.core.model

sealed interface MarketResult<out T> {

    data class Success<T>(
        val data: T
    ) : MarketResult<T>

    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : MarketResult<Nothing>

    data object Loading : MarketResult<Nothing>
}
