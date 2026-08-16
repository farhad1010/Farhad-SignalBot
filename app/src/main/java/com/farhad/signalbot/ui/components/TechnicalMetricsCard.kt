package com.farhad.signalbot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farhad.signalbot.core.model.TechnicalSnapshot

@Composable
fun TechnicalMetricsCard(
    snapshot: TechnicalSnapshot?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text(
                text = "TECHNICAL ANALYSIS",
                style = MaterialTheme.typography.titleMedium
            )

            MetricRow(
                "EMA Fast",
                snapshot?.emaFast
            )

            MetricRow(
                "EMA Slow",
                snapshot?.emaSlow
            )

            MetricRow(
                "RSI (14)",
                snapshot?.rsi
            )

            MetricRow(
                "MACD",
                snapshot?.macd
            )

            MetricRow(
                "MACD Signal",
                snapshot?.macdSignal
            )

            MetricRow(
                "Bullish Score",
                snapshot?.bullishScore
            )

            MetricRow(
                "Bearish Score",
                snapshot?.bearishScore
            )
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: Double?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {
        Text(label)

        Text(
            value?.let {
                "%.2f".format(it)
            } ?: "--"
        )
    }
}
