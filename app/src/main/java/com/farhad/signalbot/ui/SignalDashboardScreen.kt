package com.farhad.signalbot.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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

    val dashboardViewModel:
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
        dashboardViewModel.state.collectAsState()

    val snapshot =
        when (
            val signalState =
                state.signalState
        ) {

            is SignalState.Ready ->
                signalState.analysis

            is SignalState.NoSignal ->
                signalState.analysis

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
            text = "SignalBot",
            style =
                MaterialTheme.typography
                    .headlineMedium
        )

        Text(
            text =
                "Real Market Intelligence",
            style =
                MaterialTheme.typography
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
            snapshot = snapshot
        )

        Button(
            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                !state.isRefreshing,

            onClick =
                dashboardViewModel::refreshNow
        ) {

            if (state.isRefreshing) {

                CircularProgressIndicator()

            } else {

                Text(
                    text =
                        "Refresh Market"
                )
            }
        }

        state.errorMessage?.let { error ->

            Text(
                text = error,
                color =
                    MaterialTheme.colorScheme.error
            )
        }
    }
}
