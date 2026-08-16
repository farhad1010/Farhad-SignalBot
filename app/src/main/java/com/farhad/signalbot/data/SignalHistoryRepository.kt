package com.farhad.signalbot.data

import com.farhad.signalbot.core.model.SignalRecord
import kotlinx.coroutines.flow.Flow

class SignalHistoryRepository(
    private val store: SignalHistoryStore
) {

    val history: Flow<List<SignalRecord>>
        get() = store.signals

    suspend fun save(record: SignalRecord) {
        store.add(record)
    }

    suspend fun update(record: SignalRecord) {
        store.update(record)
    }

    suspend fun clear() {
        store.clear()
    }
}
