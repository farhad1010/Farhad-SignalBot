package com.farhad.signalbot.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface MarketApiService {

    @GET("v2/aggs/ticker/{ticker}/range/1/minute/1/day")
    suspend fun getMinuteBars(
        @retrofit2.http.Path("ticker") ticker: String,
        @Query("adjusted") adjusted: Boolean = true,
        @Query("sort") sort: String = "asc",
        @Query("limit") limit: Int = 500
    ): PolygonBarsResponse
}
