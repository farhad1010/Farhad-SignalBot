package com.farhad.signalbot.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farhad.signalbot.core.di.AppContainer
import com.farhad.signalbot.core.model.MarketCandle
import com.farhad.signalbot.core.model.SignalOutcome
import com.farhad.signalbot.core.model.SignalRecord
import com.farhad.signalbot.core.model.SignalState
import com.farhad.signalbot.core.model.SignalDirection
import com.farhad.signalbot.core.model.TradingSignal
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant

class SignalDashboardViewModel(
    private val container: AppContainer,
    private val historyRepository: SignalHistoryRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow(
            SignalDashboardState()
        )

    val state: StateFlow<SignalDashboardState> =
        _state.asStateFlow()

    private val signalFactory =
        SignalFactory()

    private val signalEvaluator =
        SignalEvaluator()

    private var refreshJob: Job? = null

    private var lastSavedSignalKey: String? = null

    init {
        startLiveUpdates()
    }

    fun selectSymbol(
        symbol: TradingSymbol
    ) {

        if (
            _state.value.selectedSymbol == symbol
        ) {
            return
        }

        refreshJob?.cancel()

        _state.value =
            SignalDashboardState(
                selectedSymbol = symbol
            )

        lastSavedSignalKey = null

        startLiveUpdates()
    }

    fun refreshNow() {

        viewModelScope.launch {
            loadMarketData()
        }
    }

    private fun startLiveUpdates() {

        refreshJob?.cancel()

        refreshJob =
            viewModelScope.launch {

                while (isActive) {

                    loadMarketData()

                    delay(
                        REFRESH_INTERVAL_MILLIS
                    )
                }
            }
    }

    private suspend fun loadMarketData() {

        val current =
            _state.value

        _state.value =
            current.copy(
                isRefreshing = true,
                errorMessage = null,
                signalState =
                    if (current.candles.isEmpty()) {
                        SignalState.Loading
                    } else {
                        current.signalState
                    }
            )

        val result =
            container
                .marketDataRepository
                .getCandles(
                    symbol =
                        current.selectedSymbol,
                    limit = CANDLE_LIMIT
                )

        result
            .onSuccess { candles ->

                processMarketData(
                    candles
                )
            }
            .onFailure { error ->

                _state.value =
                    _state.value.copy(
                        isRefreshing = false,
                        errorMessage =
                            error.message
                                ?: "Unable to load market data."
                    )
            }
    }

    private suspend fun processMarketData(
        candles: List<MarketCandle>
    ) {

        if (candles.size < MINIMUM_CANDLES) {

            _state.value =
                _state.value.copy(
                    candles = candles,
                    isRefreshing = false,
                    errorMessage =
                        "Insufficient market data. " +
                            "At least $MINIMUM_CANDLES " +
                            "candles are required."
                )

            return
        }

        val currentPrice =
            candles.last().close

        val previousPrice =
            _state.value.currentPrice

        val changePercent =
            previousPrice
                ?.takeIf { it > 0.0 }
                ?.let {
                    (
                        (currentPrice - it) /
                            it
                    ) * 100.0
                }

        val analysis =
            container
                .signalEngine
                .analyze(candles)

        val signalState =
            analysis.fold(

                onSuccess = { result ->

                    signalFactory.create(
                        symbol =
                            _state.value.selectedSymbol,
                        analysis = result,
                        timeframeSeconds =
                            SIGNAL_TIMEFRAME_SECONDS
                    )
                },

                onFailure = { error ->

                    SignalState.Error(
                        error.message
                            ?: "Market analysis failed."
                    )
                }
            )

        _state.value =
            _state.value.copy(
                candles = candles,
                previousPrice = previousPrice,
                currentPrice = currentPrice,
                priceChangePercent =
                    changePercent,
                signalState = signalState,
                isRefreshing = false,
                lastUpdatedMillis =
                    System.currentTimeMillis(),
                errorMessage = null
            )

        saveAndEvaluateSignal(
            signalState = signalState,
            candles = candles
        )
    }

    private suspend fun saveAndEvaluateSignal(
        signalState: SignalState,
        candles: List<MarketCandle>
    ) {

        if (signalState !is SignalState.Ready) {
            return
        }

        val signal =
            signalState.signal

        val signalKey =
            buildSignalKey(
                signal
            )

        if (
            signalKey != lastSavedSignalKey
        ) {

            val record =
                SignalRecord(
                    signal = signal,
                    outcome =
                        SignalOutcome.PENDING
                )

            historyRepository.save(
                record
            )

            lastSavedSignalKey =
                signalKey
        }

        evaluatePendingSignals(
            candles
        )
    }

    private suspend fun evaluatePendingSignals(
        candles: List<MarketCandle>
    ) {

        val now =
            Instant.now()

        historyRepository.history
            .collectOnce()
            .forEach { record ->

                if (
                    record.outcome !=
                    SignalOutcome.PENDING
                ) {
                    return@forEach
                }

                val evaluated =
                    signalEvaluator.evaluate(
                        record = record,
                        candles = candles,
                        now = now
                    )

                if (
                    evaluated.outcome !=
                    SignalOutcome.PENDING
                ) {

                    historyRepository.update(
                        evaluated
                    )
                }
            }
    }

    private fun buildSignalKey(
        signal: TradingSignal
    ): String {

        return listOf(
            signal.symbol.id,
            signal.direction.name,
            signal.sourcePrice.toString(),
            signal.generatedAt.epochSecond
        ).joinToString("|")
    }

    override fun onCleared() {

        refreshJob?.cancel()

        super.onCleared()
    }

    private companion object {

        const val CANDLE_LIMIT = 200

        const val MINIMUM_CANDLES = 50

        const val SIGNAL_TIMEFRAME_SECONDS =
            60L

        const val REFRESH_INTERVAL_MILLIS =
            15_000L
    }
}

private suspend fun <T> kotlinx.coroutines.flow.Flow<T>.collectOnce(): T? {

    var result: T? = null

    kotlinx.coroutines.flow.firstOrNull()?.let {
        result = it
    }

    return result
}
