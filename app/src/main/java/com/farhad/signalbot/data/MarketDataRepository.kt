package com.farhad.signalbot.data

import com.farhad.signalbot.core.model.MarketCandle
import com.farhad.signalbot.core.model.TradingSymbol
import com.farhad.signalbot.data.remote.MarketApiClient
import java.time.Instant

class MarketDataRepository(
    private val apiClient: MarketApiClient
) {

    suspend fun getCandles(
        symbol: TradingSymbol,
        limit: Int = 5000
    ): Result<List<MarketCandle>> {

        return apiClient
            .getSecondBars(
                symbol = symbol.providerSymbol,
                limit = limit
            )
            .map { response ->

                response.results
                    .sortedBy {
                        it.timestampMillis
                    }
                    .map { bar ->

                        val openTime =
                            Instant.ofEpochMilli(
                                bar.timestampMillis
                            )

                        MarketCandle(
                            openTime = openTime,

                            closeTime =
                                openTime.plusSeconds(1),

                            open = bar.open,

                            high = bar.high,

                            low = bar.low,

                            close = bar.close,

                            volume = bar.volume
                        )
                    }
            }
    }

    suspend fun getPreviousDayClose(
        symbol: TradingSymbol
    ): Result<Double> {

        return apiClient.getPreviousDayClose(
            symbol.providerSymbol
        )
    }
}
