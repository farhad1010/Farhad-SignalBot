package com.farhad.signalbot.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalDao {

    @Query(
        """
        SELECT * FROM signal_history
        ORDER BY generatedAtMillis DESC
        LIMIT :limit
        """
    )
    fun observeRecent(limit: Int = 100): Flow<List<SignalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(signal: SignalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(signals: List<SignalEntity>)

    @Delete
    suspend fun delete(signal: SignalEntity)

    @Query("DELETE FROM signal_history")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM signal_history")
    suspend fun count(): Int
}
