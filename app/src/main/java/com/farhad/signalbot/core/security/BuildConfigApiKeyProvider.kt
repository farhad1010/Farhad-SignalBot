package com.farhad.signalbot.core.security

import com.farhad.signalbot.BuildConfig

class BuildConfigApiKeyProvider : ApiKeyProvider {

    override fun getMarketApiKey(): String {
        return BuildConfig.MARKET_API_KEY
    }
}
