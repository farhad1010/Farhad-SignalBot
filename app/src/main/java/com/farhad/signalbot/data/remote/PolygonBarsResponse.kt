package com.farhad.signalbot.data.remote

import com.google.gson.annotations.SerializedName

data class PolygonBarsResponse(
    @SerializedName("results")
    val results: List<PolygonBar> = emptyList(),

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("resultsCount")
    val resultsCount: Int = 0,

    @SerializedName("request_id")
    val requestId: String? = null,

    @SerializedName("error")
    val error: String? = null,

    @SerializedName("message")
    val message: String? = null
)

data class PolygonBar(
    @SerializedName("o")
    val open: Double,

    @SerializedName("h")
    val high: Double,

    @SerializedName("l")
    val low: Double,

    @SerializedName("c")
    val close: Double,

    @SerializedName("v")
    val volume: Double,

    @SerializedName("t")
    val timestampMillis: Long
)
