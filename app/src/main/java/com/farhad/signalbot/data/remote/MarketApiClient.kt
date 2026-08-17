package com.farhad.signalbot.data.remote

import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneOffset

class MarketApiClient(
    private val service: MarketApiService
) {

    suspend fun getMinuteBars(
        symbol: String,
        limit: Int = 500
    ): Result<PolygonBarsResponse> {

        return try {

            val today =
                LocalDate.now(
                    ZoneOffset.UTC
                )

            val from =
                today.minusDays(7)

            val response =
                service.getMinuteBars(
                    ticker = symbol,
                    multiplier = 1,
                    timespan = "minute",
                    from = from.toString(),
                    to = today.toString(),
                    adjusted = false,
                    sort = "asc",
                    limit =
                        limit.coerceIn(
                            100,
                            5000
                        )
                )

            when {

                response.results.isEmpty() ->
                    Result.failure(
                        MarketApiException.InvalidResponse(
                            response.message
                                ?: "No market data returned."
                        )
                    )

                response.error != null ->
                    Result.failure(
                        MarketApiException.InvalidResponse(
                            response.error
                        )
                    )

                else ->
                    Result.success(
                        response
                    )
            }

        } catch (
            e: HttpException
        ) {

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

        } catch (
            e: IOException
        ) {

            Result.failure(
                MarketApiException.Network(e)
            )

        } catch (
            e: Exception
        ) {

            Result.failure(
                MarketApiException.Unknown(e)
            )
        }
    }
}
