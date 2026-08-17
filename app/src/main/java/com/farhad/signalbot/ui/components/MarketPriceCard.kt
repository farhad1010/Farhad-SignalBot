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
        modifier =
            modifier.fillMaxWidth(),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            )
    ) {

        Column(
            modifier =
                Modifier.padding(22.dp),

            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = symbol,

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall
                )

                Text(
                    text = "LIVE ●",

                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                )
            }

            Text(
                text =
                    price?.let {

                        String.format(
                            Locale.US,
                            "%.5f",
                            it
                        )

                    } ?: "--",

                style =
                    MaterialTheme
                        .typography
                        .displaySmall
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    "LIVE MOVEMENT"
                )

                Text(
                    text =
                        changePercent?.let {

                            String.format(
                                Locale.US,
                                "%+.3f%%",
                                it
                            )

                        } ?: "--"
                )
            }
        }
    }
}
