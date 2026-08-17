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

    /*
     * We create at most one signal for the same
     * market candle. This prevents the 15-second
     * refresh loop from creating duplicate signals.
     */
    private var lastSignalCandleTime: Instant? = null

    init {
        startLiveUpdates()
    }

    fun selectSymbol(
        symbol: TradingSymbol
    ) {
        if (_state.value.selectedSymbol == symbol) {
            return
        }

        refreshJob?.cancel()

        lastSignalCandleTime = null

        _state.value =
            SignalDashboardState(
                selectedSymbol = symbol
            )

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

        val currentState =
            _state.value

        _state.value =
            currentState.copy(
                isRefreshing = true,
                errorMessage = null,
                signalState =
                    if (currentState.candles.isEmpty()) {
                        SignalState.Loading
                    } else {
                        currentState.signalState
                    }
            )

        val result =
            container.marketDataRepository.getCandles(
                symbol =
                    currentState.selectedSymbol,
                limit = CANDLE_LIMIT
            )

        result
            .onSuccess { candles ->
                processMarketData(candles)
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

        val previousPrice =
            _state.value.currentPrice

        val currentPrice =
            candles.last().close

        val changePercent =
            previousPrice
                ?.takeIf { it > 0.0 }
                ?.let { previous ->
                    (
                        (currentPrice - previous) /
                            previous
                        ) * 100.0
                }

        val analysis =
            container.signalEngine
                .analyze(candles)

        val signalState =
            analysis.fold(
                onSuccess = { analysisResult ->

                    signalFactory.create(
                        symbol =
                            _state.value.selectedSymbol,
                        analysis =
                            analysisResult,
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

        /*
         * First evaluate older pending signals.
         */
        evaluatePendingSignals(candles)

        /*
         * Then save the new signal only once
         * for the current market candle.
         */
        saveNewSignalIfNeeded(
            signalState = signalState,
            latestCandle = candles.last()
        )
    }

    private suspend fun saveNewSignalIfNeeded(
        signalState: SignalState,
        latestCandle: MarketCandle
    ) {

        if (signalState !is SignalState.Ready) {
            return
        }

        if (
            lastSignalCandleTime ==
            latestCandle.openTime
        ) {
            return
        }

        val signal =
            signalState.signal

        /*
         * A signal generated from a candle is tied
         * to that candle. The same candle must not
         * produce duplicate history records.
         */
        val existingRecords =
            historyRepository.history.first()

        val alreadyExists =
            existingRecords.any {
                it.signal.symbol.id ==
                    signal.symbol.id &&
                    it.signal.generatedAt
                        .isAfter(
                            latestCandle.openTime
                        )
            }

        if (!alreadyExists) {

            historyRepository.save(
                SignalRecord(
                    signal = signal,
                    outcome =
                        SignalOutcome.PENDING
                )
            )
        }

        lastSignalCandleTime =
            latestCandle.openTime
    }

    private suspend fun evaluatePendingSignals(
        candles: List<MarketCandle>
    ) {

        val records =
            historyRepository.history.first()

        val now =
            Instant.now()

        records
            .filter {
                it.outcome ==
                    SignalOutcome.PENDING
            }
            .forEach { record ->

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

    override fun onCleared() {

        refreshJob?.cancel()

        super.onCleared()
    }

    private companion object {

        const val CANDLE_LIMIT = 200

        const val MINIMUM_CANDLES = 50

        const val SIGNAL_TIMEFRAME_SECONDS = 60L

        const val REFRESH_INTERVAL_MILLIS = 15_000L
    }
}
