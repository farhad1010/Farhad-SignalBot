package com.farhad.signalbot.core.di

import android.content.Context
import androidx.room.Room
import com.farhad.signalbot.data.local.SignalDatabase

object DatabaseProvider {

    @Volatile
    private var instance: SignalDatabase? = null

    fun get(
        context: Context
    ): SignalDatabase {

        return instance
            ?: synchronized(this) {

                instance
                    ?: Room.databaseBuilder(
                        context.applicationContext,
                        SignalDatabase::class.java,
                        DATABASE_NAME
                    )
                        .build()
                        .also {
                            instance = it
                        }
            }
    }

    private const val DATABASE_NAME =
        "signalbot.db"
}
