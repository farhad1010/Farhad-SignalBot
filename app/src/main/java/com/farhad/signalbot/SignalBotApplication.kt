package com.farhad.signalbot

import android.app.Application
import com.farhad.signalbot.core.di.AppContainer

class SignalBotApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this)
    }
}
