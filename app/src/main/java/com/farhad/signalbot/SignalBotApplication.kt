package com.farhad.signalbot

import android.app.Application
import com.farhad.signalbot.core.di.AppContainer
import com.farhad.signalbot.core.di.DatabaseProvider
import com.farhad.signalbot.data.RoomSignalHistoryStore
import com.farhad.signalbot.data.SignalHistoryRepository

class SignalBotApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    lateinit var historyRepository: SignalHistoryRepository
        private set

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this)

        val database = DatabaseProvider.get(this)

        val historyStore =
            RoomSignalHistoryStore(
                dao = database.signalDao()
            )

        historyRepository =
            SignalHistoryRepository(
                store = historyStore
            )
    }
}
