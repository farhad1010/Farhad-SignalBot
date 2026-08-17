package com.farhad.signalbot.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.farhad.signalbot.core.model.SignalDirection
import com.farhad.signalbot.core.model.SignalState
import com.farhad.signalbot.core.model.SignalStrength

@Composable
fun SignalStatusCard(
    state: SignalState,
    modifier: Modifier = Modifier
) {

    val accent =
        when (state) {

            is SignalState.Ready -> {
                when (
                    state.signal.direction
                ) {

                    SignalDirection.CALL ->
                        Color(0xFF00E676)

                    SignalDirection.PUT ->
                        Color(0xFFFF5252)

                    SignalDirection.NEUTRAL ->
                        Color(0xFFFFC107)
                }
            }

            is SignalState.NoSignal ->
                Color(0xFFFFC107)

            is SignalState.Error ->
                Color(0xFFFF5252)

            else ->
                MaterialTheme
                    .colorScheme
                    .primary
        }

    Card(
        modifier =
            modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(28.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFF111522)
            )
    ) {

        Column(
            modifier =
                Modifier.padding(24.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text =
                    "AI MARKET SIGNAL",

                style =
                    MaterialTheme
                        .typography
                        .labelLarge
            )

            when (state) {

                SignalState.Idle -> {

                    Text(
                        "Waiting for live market data"
                    )
                }

                SignalState.Loading -> {

                    Text(
                        "Analyzing live market data..."
                    )
                }

                is SignalState.Error -> {

                    Text(
                        text = state.message,
                        color = accent
                    )
                }

                is SignalState.NoSignal -> {

                    Text(
                        text = "NO TRADE",
                        color = accent,
                        style =
                            MaterialTheme
                                .typography
                                .headlineLarge
                    )

                    Text(
                        "Market conditions are not strong enough."
                    )
                }

                is SignalState.Ready -> {

                    val signal =
                        state.signal

                    Text(
                        text =
                            when (
                                signal.direction
                            ) {

                                SignalDirection.CALL ->
                                    "UP"

                                SignalDirection.PUT ->
                                    "DOWN"

                                SignalDirection.NEUTRAL ->
                                    "NO TRADE"
                            },

                        color = accent,

                        style =
                            MaterialTheme
                                .typography
                                .displaySmall
                    )

                    Text(
                        text =
                            "${signal.confidence.toInt()}% CONFIDENCE",

                        color = accent,

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFF191E2D),
                                    RoundedCornerShape(
                                        18.dp
                                    )
                                )
                                .padding(16.dp),

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Column {

                            Text(
                                "EXPIRY"
                            )

                            Text(
                                "${signal.timeframeSeconds} seconds",
                                color = accent
                            )
                        }

                        Column {

                            Text(
                                "ENTRY"
                            )

                            Text(
                                "%.5f".format(
                                    signal.sourcePrice
                                )
                            )
                        }
                    }

                    Text(
                        text =
                            when (
                                signal.strength
                            ) {

                                SignalStrength.VERY_STRONG ->
                                    "★★★★★ VERY STRONG"

                                SignalStrength.STRONG ->
                                    "★★★★ STRONG"

                                SignalStrength.MODERATE ->
                                    "★★★ MODERATE"

                                SignalStrength.WEAK ->
                                    "★★ WEAK"
                            },

                        color = accent
                    )
                }
            }
        }
    }
}
