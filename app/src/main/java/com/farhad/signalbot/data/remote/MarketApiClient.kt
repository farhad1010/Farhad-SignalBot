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
        limit: Int = 5000
    ): Result<PolygonBarsResponse> {

        return try {

            val today = LocalDate.now(ZoneOffset.UTC)

            val response = service.getBars(
                ticker = symbol,
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
                                ?: "No live market data returned."
                        )
                    )
                }

                else -> {
                    Result.success(
                        response.copy(
                            results = response.results
                                .sortedBy {
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
                            "Market data server error."
                        )

                    else ->
                        MarketApiException.Server(
                            "HTTP ${e.code()}"
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

            val response =
                service.getPreviousDayBar(
                    ticker = symbol,
                    adjusted = false
                )

            val close =
                response.results
                    .firstOrNull()
                    ?.close

            if (close == null || close <= 0.0) {

                Result.failure(
                    MarketApiException.InvalidResponse(
                        "Previous market close unavailable."
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

                    else ->
                        MarketApiException.Server(
                            "Previous close HTTP ${e.code()}"
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
}
