package com.farhad.signalbot.data.remote

import com.google.gson.annotations.SerializedName

data class MarketTickerDto(

    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("lastPrice")
    val lastPrice: String,

    @SerializedName("priceChangePercent")
    val priceChangePercent: String,

    @SerializedName("volume")
    val volume: String
)
