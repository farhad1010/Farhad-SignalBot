package com.farhad.signalbot.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarketApiService {

    @GET("api/v3/ticker/24hr")
    suspend fun ticker24h(
        @Query("symbol")
        symbol: String
    ): MarketTickerDto

    @GET("api/v3/klines")
    suspend fun candles(
        @Query("symbol")
        symbol: String,

        @Query("interval")
        interval: String,

        @Query("limit")
        limit: Int = 200
    ): List<List<Any>>
}
