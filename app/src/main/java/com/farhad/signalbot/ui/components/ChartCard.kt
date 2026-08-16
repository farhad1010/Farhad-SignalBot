package com.farhad.signalbot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farhad.signalbot.core.model.MarketCandle
import com.farhad.signalbot.ui.chart.MarketChart
import com.farhad.signalbot.ui.chart.MarketChartData

@Composable
fun ChartCard(
    candles: List<MarketCandle>,
    modifier: Modifier = Modifier
) {
    if (candles.isEmpty()) return

    val chartData =
        MarketChartData.from(candles)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "MARKET CHART",
                modifier = Modifier.padding(
                    horizontal = 8.dp
                ),
                style =
                    MaterialTheme.typography.titleMedium
            )

            MarketChart(
                data = chartData
            )

            Text(
                text = "EMA 12  •  EMA 26",
                modifier = Modifier.padding(
                    horizontal = 8.dp
                ),
                style =
                    MaterialTheme.typography.labelMedium
            )
        }
    }
}
