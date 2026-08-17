package com.farhad.signalbot.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.farhad.signalbot.core.di.AppContainer
import com.farhad.signalbot.core.model.SignalState
import com.farhad.signalbot.data.SignalHistoryRepository
import com.farhad.signalbot.ui.components.MarketPriceCard
import com.farhad.signalbot.ui.components.SignalStatusCard
import com.farhad.signalbot.ui.components.TechnicalMetricsCard

@Composable
fun SignalDashboardScreen(
    container: AppContainer,
    historyRepository: SignalHistoryRepository,
    modifier: Modifier = Modifier
) {

    val viewModel:
        SignalDashboardViewModel =
        viewModel(
            factory =
                SignalDashboardViewModelFactory(
                    container = container,
                    historyRepository =
                        historyRepository
                )
        )

    val state by
        viewModel.state.collectAsState()

    val snapshot =
        when (
            val signal =
                state.signalState
        ) {

            is SignalState.Ready ->
                signal.analysis

            is SignalState.NoSignal ->
                signal.analysis

            else ->
                null
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "FARHAD SIGNALBOT",
            style =
                MaterialTheme
                    .typography
                    .headlineMedium
        )

        Text(
            text =
                "Real market-data analysis",
            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )

        MarketPriceCard(
            symbol =
                state.selectedSymbol.displayName,
            price =
                state.currentPrice,
            changePercent =
                state.priceChangePercent
        )

        SignalStatusCard(
            state =
                state.signalState
        )

        TechnicalMetricsCard(
            snapshot =
                snapshot
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            Button(
                modifier =
                    Modifier.weight(1f),
                enabled =
                    !state.isRefreshing,
                onClick =
                    viewModel::refreshNow
            ) {

                if (
                    state.isRefreshing
                ) {

                    CircularProgressIndicator()

                } else {

                    Text(
                        text =
                            "Refresh"
                    )
                }
            }
        }

        Text(
            text =
                "Signals are generated from " +
                    "market data and technical " +
                    "indicators. They are not " +
                    "guaranteed trade outcomes.",
            style =
                MaterialTheme
                    .typography
                    .bodySmall
        )

        state.errorMessage?.let { error ->

            Text(
                text = error,
                color =
                    MaterialTheme
                        .colorScheme
                        .error
            )
        }
    }
}
