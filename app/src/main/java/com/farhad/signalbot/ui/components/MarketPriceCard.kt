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
import java.util.Locale

@Composable
fun MarketPriceCard(
    symbol: String,
    price: Double?,
    changePercent: Double?,
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = symbol,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = price?.let {
                    String.format(
                        Locale.US,
                        "%.4f",
                        it
                    )
                } ?: "--",
                style = MaterialTheme.typography.headlineLarge
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "24h movement"
                )

                Text(
                    text = changePercent?.let {
                        String.format(
                            Locale.US,
                            "%+.2f%%",
                            it
                        )
                    } ?: "--"
                )
            }
        }
    }
}
