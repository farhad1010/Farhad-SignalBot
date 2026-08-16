package com.farhad.signalbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.farhad.signalbot.ui.SignalBotApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application =
            application as SignalBotApplication

        setContent {

            SignalBotApp(
                container = application.appContainer,
                historyRepository =
                    application.historyRepository
            )
        }
    }
}
