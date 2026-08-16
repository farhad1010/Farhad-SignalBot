package com.farhad.signalbot.data

import com.farhad.signalbot.core.model.SignalRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemorySignalHistoryStore : SignalHistoryStore {

    private val _signals =
        MutableStateFlow<List<SignalRecord>>(emptyList())

    override val signals: StateFlow<List<SignalRecord>> =
        _signals.asStateFlow()

    override suspend fun add(record: SignalRecord) {
        _signals.value =
            listOf(record) + _signals.value
                .take(MAX_HISTORY - 1)
    }

    override suspend fun update(record: SignalRecord) {
        _signals.value =
            _signals.value.map { existing ->
                if (existing.signal.id == record.signal.id) {
                    record
                } else {
                    existing
                }
            }
    }

    override suspend fun clear() {
        _signals.value = emptyList()
    }

    private companion object {
        const val MAX_HISTORY = 100
    }
}
