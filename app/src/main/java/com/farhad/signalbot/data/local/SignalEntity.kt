package com.farhad.signalbot.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signal_history")
data class SignalEntity(
    @PrimaryKey
    val id: String,
    val symbolId: String,
    val symbolName: String,
    val providerSymbol: String,
    val direction: String,
    val strength: String,
    val confidence: Double,
    val generatedAtMillis: Long,
    val sourcePrice: Double,
    val timeframeSeconds: Long,
    val expiresAtMillis: Long,
    val outcome: String,
    val evaluatedAtMillis: Long?,
    val resultPrice: Double?,
    val reasons: String
)
