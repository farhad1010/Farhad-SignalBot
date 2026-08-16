package com.farhad.signalbot.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SignalEntity::class],
    version = 1,
    exportSchema = true
)
abstract class SignalDatabase : RoomDatabase() {

    abstract fun signalDao(): SignalDao
}
