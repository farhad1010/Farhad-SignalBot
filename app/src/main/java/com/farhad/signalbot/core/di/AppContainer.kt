package com.farhad.signalbot.core.di

import android.content.Context
import com.farhad.signalbot.core.security.ApiKeyProvider
import com.farhad.signalbot.core.security.BuildConfigApiKeyProvider
import com.farhad.signalbot.data.MarketDataRepository
import com.farhad.signalbot.data.remote.RetrofitProvider
import com.farhad.signalbot.domain.SignalEngine

class AppContainer(
    context: Context
) {

    private val appContext = context.applicationContext

    val apiKeyProvider: ApiKeyProvider =
        BuildConfigApiKeyProvider()

    val marketDataRepository: MarketDataRepository by lazy {

        val apiKey = apiKeyProvider.getMarketApiKey()

        require(apiKey.isNotBlank()) {
            "MARKET_API_KEY is not configured."
        }

        MarketDataRepository(
            apiClient = RetrofitProvider.create(apiKey)
        )
    }

    val signalEngine: SignalEngine by lazy {
        SignalEngine()
    }
}
