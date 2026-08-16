package com.farhad.signalbot.core.di

import android.content.Context
import androidx.room.Room
import com.farhad.signalbot.data.local.SignalDatabase

object DatabaseProvider {

    @Volatile
    private var INSTANCE: SignalDatabase? = null

    fun get(context: Context): SignalDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                SignalDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
    }

    private const val DATABASE_NAME = "signalbot.db"
}
