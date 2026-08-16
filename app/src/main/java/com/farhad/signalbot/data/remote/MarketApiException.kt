package com.farhad.signalbot.data.remote

sealed class MarketApiException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class Network(cause: Throwable) :
        MarketApiException("Unable to connect to the market-data server.", cause)

    class Unauthorized :
        MarketApiException("Market-data API authorization failed.")

    class RateLimited :
        MarketApiException("Market-data API rate limit reached.")

    class Server(message: String) :
        MarketApiException(message)

    class InvalidResponse(message: String) :
        MarketApiException(message)

    class Unknown(cause: Throwable) :
        MarketApiException("Unexpected market-data error.", cause)
}
