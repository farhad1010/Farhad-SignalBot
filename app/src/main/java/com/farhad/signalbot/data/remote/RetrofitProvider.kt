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

                    val originalRequest =
                        chain.request()

                    val requestBuilder =
                        originalRequest
                            .newBuilder()

                    /*
                     * Only attach the API key when it exists.
                     * This prevents the application from crashing
                     * during startup if the key is unavailable.
                     */
                    if (apiKey.isNotBlank()) {
                        val url =
                            originalRequest.url
                                .newBuilder()
                                .addQueryParameter(
                                    "apiKey",
                                    apiKey.trim()
                                )
                                .build()

                        requestBuilder.url(url)
                    }

                    chain.proceed(
                        requestBuilder.build()
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
