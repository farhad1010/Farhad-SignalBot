package com.farhad.signalbot.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitProvider {

    private const val BASE_URL =
        "https://api.massive.com/"

    fun create(
        apiKey: String
    ): MarketApiClient {

        require(apiKey.isNotBlank()) {
            "Massive API key is missing."
        }

        val client =
            OkHttpClient.Builder()
                .connectTimeout(
                    15,
                    TimeUnit.SECONDS
                )
                .readTimeout(
                    30,
                    TimeUnit.SECONDS
                )
                .writeTimeout(
                    30,
                    TimeUnit.SECONDS
                )
                .retryOnConnectionFailure(true)
                .addInterceptor { chain ->

                    val request =
                        chain.request()

                    val url =
                        request.url
                            .newBuilder()
                            .addQueryParameter(
                                "apiKey",
                                apiKey
                            )
                            .build()

                    chain.proceed(
                        request.newBuilder()
                            .url(url)
                            .build()
                    )
                }
                .build()

        val retrofit =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()

        return MarketApiClient(
            retrofit.create(
                MarketApiService::class.java
            )
        )
    }
}
