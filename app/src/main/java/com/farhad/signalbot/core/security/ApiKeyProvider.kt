package com.farhad.signalbot.core.security

interface ApiKeyProvider {

    fun getMarketApiKey(): String
}
