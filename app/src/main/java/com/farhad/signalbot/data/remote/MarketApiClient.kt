package com.farhad.signalbot.data.remote

import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneOffset

class MarketApiClient(
    private val service: MarketApiService
) {

    suspend fun getSecondBars(
        symbol: String,
        limit: Int
    ): Result<PolygonBarsResponse> {

        return try {

            val ticker = normalizeForexTicker(symbol)

            val today = LocalDate.now(ZoneOffset.UTC)

            val response = service.getBars(
                ticker = ticker,
                multiplier = 1,
                timespan = "second",
                from = today.minusDays(2).toString(),
                to = today.toString(),
                adjusted = false,
                sort = "desc",
                limit = limit.coerceIn(100, 5000)
            )

            when {

                response.error != null -> {
                    Result.failure(
                        MarketApiException.InvalidResponse(
                            response.error
                        )
                    )
                }

                response.results.isEmpty() -> {
                    Result.failure(
                        MarketApiException.InvalidResponse(
                            response.message
                                ?: "No market data returned for $ticker."
                        )
                    )
                }

                else -> {
                    Result.success(
                        response.copy(
                            results = response.results.sortedBy {
                                it.timestampMillis
                            }
                        )
                    )
                }
            }

        } catch (e: HttpException) {

            Result.failure(
                when (e.code()) {

                    401, 403 ->
                        MarketApiException.Unauthorized()

                    429 ->
                        MarketApiException.RateLimited()

                    in 500..599 ->
                        MarketApiException.Server(
                            "Massive market-data server error."
                        )

                    else ->
                        MarketApiException.Server(
                            "Massive HTTP ${e.code()}"
                        )
                }
            )

        } catch (e: IOException) {

            Result.failure(
                MarketApiException.Network(e)

            )

        } catch (e: Exception) {

            Result.failure(
                MarketApiException.Unknown(e)
            )
        }
    }

    suspend fun getPreviousDayClose(
        symbol: String
    ): Result<Double> {

        return try {

            val ticker = normalizeForexTicker(symbol)

            val response =
                service.getPreviousDayBar(
                    ticker = ticker,
                    adjusted = false
                )

            val close =
                response.results
                    .firstOrNull()
                    ?.close

            if (close == null || close <= 0.0) {

                Result.failure(
                    MarketApiException.InvalidResponse(
                        "Previous close unavailable for $ticker."
                    )
                )

            } else {

                Result.success(close)
            }

        } catch (e: HttpException) {

            Result.failure(
                when (e.code()) {

                    401, 403 ->
                        MarketApiException.Unauthorized()

                    429 ->
                        MarketApiException.RateLimited()

                    in 500..599 ->
                        MarketApiException.Server(
                            "Massive market-data server error."
                        )

                    else ->
                        MarketApiException.Server(
                            "Massive HTTP ${e.code()}"
                        )
                }
            )

        } catch (e: IOException) {

            Result.failure(
                MarketApiException.Network(e)
            )

        } catch (e: Exception) {

            Result.failure(
                MarketApiException.Unknown(e)
            )
        }
    }

    private fun normalizeForexTicker(
        symbol: String
    ): String {

        val clean = symbol
            .trim()
            .uppercase()
            .replace("/", "")
            .replace("-", "")

        return when {

            clean.startsWith("C:") ->
                clean

            clean.length == 6 ->
                "C:$clean"

            else ->
                symbol.trim()
        }
    }
}
