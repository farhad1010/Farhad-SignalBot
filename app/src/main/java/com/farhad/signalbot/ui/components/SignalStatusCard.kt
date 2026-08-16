package com.farhad.signalbot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farhad.signalbot.core.model.SignalDirection
import com.farhad.signalbot.core.model.SignalState
import com.farhad.signalbot.core.model.SignalStrength

@Composable
fun SignalStatusCard(
    state: SignalState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text(
                text = "AI MARKET SIGNAL",
                style = MaterialTheme.typography.labelLarge
            )

            when (state) {

                SignalState.Idle -> {
                    Text("Waiting for market data")
                }

                SignalState.Loading -> {
                    Text("Analyzing live market data…")
                }

                is SignalState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is SignalState.NoSignal -> {
                    Text(
                        text = "NO TRADE",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Market conditions are not strong enough."
                    )
                }

                is SignalState.Ready -> {

                    val signal = state.signal

                    Text(
                        text = when (signal.direction) {
                            SignalDirection.CALL -> "CALL"
                            SignalDirection.PUT -> "PUT"
                            SignalDirection.NEUTRAL -> "NO TRADE"
                        },
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Confidence"
                        )

                        Text(
                            "${signal.confidence.toInt()}%"
                        )
                    }

                    Text(
                        text = when (signal.strength) {
                            SignalStrength.VERY_STRONG ->
                                "Very Strong"

                            SignalStrength.STRONG ->
                                "Strong"

                            SignalStrength.MODERATE ->
                                "Moderate"

                            SignalStrength.WEAK ->
                                "Weak"
                        }
                    )

                    Text(
                        text =
                            "Entry reference: ${
                                signal.sourcePrice
                            }"
                    )
                }
            }
        }
    }
}
