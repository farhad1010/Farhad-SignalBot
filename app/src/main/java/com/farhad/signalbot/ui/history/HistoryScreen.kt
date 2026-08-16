package com.farhad.signalbot.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.farhad.signalbot.data.SignalHistoryRepository

@Composable
fun HistoryScreen(
    repository: SignalHistoryRepository,
    modifier: Modifier = Modifier
) {
    val viewModel: HistoryViewModel =
        viewModel(
            factory =
                HistoryViewModelFactory(repository)
        )

    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "Signal History",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Text(
            text =
                "Total ${state.statistics.total}  •  " +
                "Wins ${state.statistics.wins}  •  " +
                "Losses ${state.statistics.losses}"
        )

        if (state.isLoading) {

            CircularProgressIndicator()

        } else if (state.records.isEmpty()) {

            Text(
                text = "No signal history yet.",
                style =
                    MaterialTheme.typography.bodyLarge
            )

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = state.records,
                    key = { it.signal.id }
                ) {
                    SignalHistoryCard(it)
                }
            }

            Button(
                onClick = viewModel::clearHistory
            ) {
                Text("Clear History")
            }
        }
    }
}
