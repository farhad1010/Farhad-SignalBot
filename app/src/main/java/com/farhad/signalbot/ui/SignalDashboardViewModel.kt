package com.farhad.signalbot.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farhad.signalbot.core.di.AppContainer
import com.farhad.signalbot.core.model.MarketCandle
import com.farhad.signalbot.core.model.SignalOutcome
import com.farhad.signalbot.core.model.SignalRecord
import com.farhad.signalbot.core.model.SignalState
import com.farhad.signalbot.core.model.TradingSymbol
import com.farhad.signalbot.data.SignalHistoryRepository
import com.farhad.signalbot.domain.SignalEvaluator
import com.farhad.signalbot.domain.SignalFactory
import com.farhad.signalbot.ui.state.SignalDashboardState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant

class SignalDashboardViewModel(
    private val container: AppContainer,
    private val historyRepository:
        SignalHistoryRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow(
            SignalDashboardState()
        )

    val state:
        StateFlow<SignalDashboardState> =
        _state.asStateFlow()

    private val factory =
        SignalFactory()

    private val evaluator =
        SignalEvaluator()

    private var refreshJob:
        Job? = null

    private var lastSignalCandle:
        Instant? = null

    init {
        startLiveUpdates()
    }

    fun selectSymbol(
        symbol: TradingSymbol
    ) {

        if (
            symbol ==
            _state.value.selectedSymbol
        ) {
            return
        }

        refreshJob?.cancel()

        lastSignalCandle = null

        _state.value =
            SignalDashboardState(
                selectedSymbol =
                    symbol
            )

        startLiveUpdates()
    }

    fun refreshNow() {

        if (
            _state.value.isRefreshing
        ) {
            return
        }

        viewModelScope.launch {
            loadMarketData()
        }
    }

    private fun startLiveUpdates() {

        refreshJob?.cancel()

        refreshJob =
            viewModelScope.launch {

                while (
                    isActive
                ) {

                    loadMarketData()

                    delay(
                        REFRESH_INTERVAL
                    )
                }
            }
    }

    private suspend fun loadMarketData() {

        val old =
            _state.value

        _state.value =
            old.copy(
                isRefreshing = true,
                errorMessage = null,

                signalState =
                    if (
                        old.candles.isEmpty()
                    ) {
                        SignalState.Loading
                    } else {
                        old.signalState
                    }
            )

        val result =
            container
                .marketDataRepository
                .getCandles(
                    symbol =
                        old.selectedSymbol,

                    limit =
                        CANDLE_LIMIT
                )

        result.fold(

            onSuccess = {
                candles ->
                processMarketData(
                    candles
                )
            },

            onFailure = {
                error ->

                _state.value =
                    _state.value.copy(
                        isRefreshing =
                            false,

                        errorMessage =
                            error.message
                                ?: "Live market data unavailable.",

                        signalState =
                            if (
                                _state.value.candles
                                    .isEmpty()
                            ) {
                                SignalState.Error(
                                    "Market data unavailable."
                                )
                            } else {
                                _state.value.signalState
                            }
                    )
            }
        )
    }

    private suspend fun processMarketData(
        candles: List<MarketCandle>
    ) {

        if (
            candles.size <
            MINIMUM_CANDLES
        ) {

            _state.value =
                _state.value.copy(
                    candles =
                        candles,

                    isRefreshing =
                        false,

                    errorMessage =
                        "Insufficient live market data."
                )

            return
        }

        val oldPrice =
            _state.value.currentPrice

        val currentPrice =
            candles.last().close

        val change =
            oldPrice
                ?.takeIf {
                    it > 0.0
                }
                ?.let {
                    (
                        (
                            currentPrice -
                                it
                            ) /
                            it
                        ) * 100.0
                }

        val analysis =
            container
                .signalEngine
                .analyze(
                    candles
                )

        val signalState =
            analysis.fold(

                onSuccess = {
                    result ->

                    factory.create(
                        symbol =
                            _state.value
                                .selectedSymbol,

                        analysis =
                            result
                    )
                },

                onFailure = {
                    error ->

                    SignalState.Error(
                        error.message
                            ?: "Analysis unavailable."
                    )
                }
            )

        _state.value =
            _state.value.copy(
                candles =
                    candles,

                previousPrice =
                    oldPrice,

                currentPrice =
                    currentPrice,

                priceChangePercent =
                    change,

                signalState =
                    signalState,

                isRefreshing =
                    false,

                lastUpdatedMillis =
                    System.currentTimeMillis(),

                errorMessage =
                    null
            )

        evaluatePending(
            candles
        )

        saveSignal(
            signalState,
            candles.last()
        )
    }

    private suspend fun saveSignal(
        signalState: SignalState,
        candle: MarketCandle
    ) {

        if (
            signalState !is
            SignalState.Ready
        ) {
            return
        }

        if (
            lastSignalCandle ==
            candle.openTime
        ) {
            return
        }

        val signal =
            signalState.signal

        val records =
            historyRepository
                .history
                .first()

        val duplicate =
            records.any {
                it.signal.symbol.id ==
                    signal.symbol.id &&
                    it.signal.generatedAt ==
                    signal.generatedAt
            }

        if (!duplicate) {

            historyRepository.save(
                SignalRecord(
                    signal =
                        signal,

                    outcome =
                        SignalOutcome.PENDING
                )
            )
        }

        lastSignalCandle =
            candle.openTime
    }

    private suspend fun evaluatePending(
        candles: List<MarketCandle>
    ) {

        val records =
            historyRepository
                .history
                .first()

        val now =
            Instant.now()

        records
            .filter {
                it.outcome ==
                    SignalOutcome.PENDING
            }
            .forEach {
                record ->

                val evaluated =
                    evaluator.evaluate(
                        record =
                            record,

                        candles =
                            candles,

                        now =
                            now
                    )

                if (
                    evaluated.outcome !=
                        SignalOutcome.PENDING
                ) {

                    historyRepository
                        .update(
                            evaluated
                        )
                }
            }
    }

    override fun onCleared() {

        refreshJob?.cancel()

        super.onCleared()
    }

    private companion object {

        const val CANDLE_LIMIT = 500

        const val MINIMUM_CANDLES = 80

        const val REFRESH_INTERVAL =
            10_000L
    }
}
