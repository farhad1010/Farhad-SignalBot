package com.farhad.signalbot.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarketApiService {

    @GET(
        "v2/aggs/ticker/{ticker}/range/{multiplier}/{timespan}/{from}/{to}"
    )
    suspend fun getBars(
        @Path("ticker")
        ticker: String,

        @Path("multiplier")
        multiplier: Int = 1,

        @Path("timespan")
        timespan: String = "second",

        @Path("from")
        from: String,

        @Path("to")
        to: String,

        @Query("adjusted")
        adjusted: Boolean = false,

        @Query("sort")
        sort: String = "desc",

        @Query("limit")
        limit: Int = 5000
    ): PolygonBarsResponse

    @GET(
        "v2/aggs/ticker/{ticker}/prev"
    )
    suspend fun getPreviousDayBar(
        @Path("ticker")
        ticker: String,

        @Query("adjusted")
        adjusted: Boolean = false
    ): PolygonBarsResponse
}
