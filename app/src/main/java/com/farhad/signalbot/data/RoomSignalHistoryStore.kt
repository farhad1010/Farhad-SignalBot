package com.farhad.signalbot.data

import com.farhad.signalbot.core.model.SignalRecord
import com.farhad.signalbot.data.local.SignalDao
import com.farhad.signalbot.data.local.SignalEntityMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomSignalHistoryStore(
    private val dao: SignalDao
) : SignalHistoryStore {

    override val signals: Flow<List<SignalRecord>> =
        dao.observeRecent()
            .map { entities ->
                entities.map(
                    SignalEntityMapper::fromEntity
                )
            }

    override suspend fun add(
        record: SignalRecord
    ) {
        dao.upsert(
            SignalEntityMapper.toEntity(record)
        )
    }

    override suspend fun update(
        record: SignalRecord
    ) {
        dao.upsert(
            SignalEntityMapper.toEntity(record)
        )
    }

    override suspend fun clear() {
        dao.deleteAll()
    }
}
