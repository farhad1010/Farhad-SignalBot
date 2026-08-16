package com.farhad.signalbot.ui.history

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
import com.farhad.signalbot.core.model.SignalOutcome
import com.farhad.signalbot.core.model.SignalRecord
import java.util.Locale

@Composable
fun SignalHistoryCard(
    record: SignalRecord,
    modifier: Modifier = Modifier
) {
    val signal = record.signal

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                Text(
                    text = signal.symbol.displayName,
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Text(
                    text = signal.direction.name,
                    style =
                        MaterialTheme.typography.titleMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                Text("Confidence")

                Text(
                    String.format(
                        Locale.US,
                        "%.1f%%",
                        signal.confidence
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                Text("Entry")

                Text(
                    String.format(
                        Locale.US,
                        "%.4f",
                        signal.sourcePrice
                    )
                )
            }

            Text(
                text = when (record.outcome) {
                    SignalOutcome.PENDING ->
                        "PENDING"

                    SignalOutcome.WIN ->
                        "WIN"

                    SignalOutcome.LOSS ->
                        "LOSS"

                    SignalOutcome.EXPIRED ->
                        "EXPIRED"

                    SignalOutcome.CANCELLED ->
                        "CANCELLED"
                },
                style =
                    MaterialTheme.typography.labelLarge
            )
        }
    }
}
