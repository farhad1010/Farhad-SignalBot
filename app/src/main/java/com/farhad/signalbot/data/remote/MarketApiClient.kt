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
        limit: Int = 200
    ): Result<PolygonBarsResponse> {

        return try {

            val safeLimit =
                limit.coerceIn(50, 500)

            val today =
                LocalDate.now(ZoneOffset.UTC)

            val from =
                today.minusDays(5)

            val to =
                today

            val response =
                service.getMinuteBars(
                    ticker = symbol,
                    from = from.toString(),
                    to = to.toString(),
                    adjusted = true,
                    sort = "asc",
                    limit = safeLimit
                )

            when {

                response.error != null ->
                    Result.failure(
                        MarketApiException.InvalidResponse(
                            response.error
                        )
                    )

                response.results.isEmpty() ->
                    Result.failure(
                        MarketApiException.InvalidResponse(
                            response.message
                                ?: "No market candles were returned."
                        )
                    )

                else ->
                    Result.success(response)
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
                            "Market-data server error: ${e.code()}"
                        )

                    else ->
                        MarketApiException.Server(
                            "Market-data request failed: ${e.code()}"
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
