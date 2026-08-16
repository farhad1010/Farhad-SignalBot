package com.farhad.signalbot.data

import com.farhad.signalbot.core.model.SignalRecord
import kotlinx.coroutines.flow.Flow

interface SignalHistoryStore {

    val signals: Flow<List<SignalRecord>>

    suspend fun add(record: SignalRecord)

    suspend fun update(record: SignalRecord)

    suspend fun clear()
}
