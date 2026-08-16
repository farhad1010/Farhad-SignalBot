package com.farhad.signalbot.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farhad.signalbot.core.model.SignalState
import com.farhad.signalbot.core.model.TradingSymbol
import com.farhad.signalbot.core.model.MarketCandle
import com.farhad.signalbot.core.di.AppContainer
import com.farhad.signalbot.domain.SignalFactory
import com.farhad.signalbot.ui.state.SignalDashboardState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

class SignalDashboardViewModel(
    private val container: AppContainer
) : ViewModel() {

    private val _state =
        MutableStateFlow(SignalDashboardState())

    val state: StateFlow<SignalDashboardState> =
        _state.asStateFlow()

    private val signalFactory = SignalFactory()

    private var refreshJob: Job? = null

    init {
        startLiveUpdates()
    }

    fun selectSymbol(symbol: TradingSymbol) {
        if (_state.value.selectedSymbol == symbol) return

        _state.value = SignalDashboardState(
            selectedSymbol = symbol
        )

        refreshNow()
    }

    fun refreshNow() {
        viewModelScope.launch {
            loadMarketData()
        }
    }

    private fun startLiveUpdates() {
        refreshJob?.cancel()

        refreshJob = viewModelScope.launch {

            while (isActive) {
                loadMarketData()
                delay(15_000L)
            }
        }
    }

    private suspend fun loadMarketData() {

        val current = _state.value

        _state.value = current.copy(
            isRefreshing = true,
            errorMessage = null,
            signalState = if (current.candles.isEmpty()) {
                SignalState.Loading
            } else {
                current.signalState
            }
        )

        val result = container.marketDataRepository.getCandles(
            symbol = current.selectedSymbol,
            limit = 200
        )

        result
            .onSuccess { candles ->
                processMarketData(candles)
            }
            .onFailure { throwable ->

                _state.value = _state.value.copy(
                    isRefreshing = false,
                    errorMessage =
                        throwable.message
                            ?: "Unable to load market data."
                )
            }
    }

    private fun processMarketData(
        candles: List<MarketCandle>
    ) {

        if (candles.isEmpty()) {
            _state.value = _state.value.copy(
                isRefreshing = false,
                errorMessage = "No market data received."
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
                ?.let {
                    ((currentPrice - it) / it) * 100.0
                }

        val analysis =
            container.signalEngine
                .analyze(candles)

        val signalState =
            analysis.fold(
                onSuccess = { result ->

                    signalFactory.create(
                        symbol = _state.value.selectedSymbol,
                        analysis = result,
                        timeframeSeconds = 60L
                    )
                },

                onFailure = {
                    SignalState.Error(
                        it.message
                            ?: "Market analysis failed."
                    )
                }
            )

        _state.value = _state.value.copy(
            candles = candles,
            previousPrice = previousPrice,
            currentPrice = currentPrice,
            priceChangePercent = changePercent,
            signalState = signalState,
            isRefreshing = false,
            lastUpdatedMillis = System.currentTimeMillis(),
            errorMessage = null
        )
    }

    override fun onCleared() {
        refreshJob?.cancel()
        super.onCleared()
    }
}
