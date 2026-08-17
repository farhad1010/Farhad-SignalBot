package com.farhad.signalbot.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarketApiService {

    @GET("v2/aggs/ticker/{ticker}/range/1/minute/{from}/{to}")
    suspend fun getMinuteBars(
        @Path("ticker")
        ticker: String,

        @Path("from")
        from: String,

        @Path("to")
        to: String,

        @Query("adjusted")
        adjusted: Boolean = true,

        @Query("sort")
        sort: String = "asc",

        @Query("limit")
        limit: Int = 500
    ): PolygonBarsResponse
}
