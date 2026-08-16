package com.farhad.signalbot.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7C8CFF),
    onPrimary = Color.White,
    secondary = Color(0xFF22D3A6),
    background = Color(0xFF080B12),
    surface = Color(0xFF101520),
    surfaceVariant = Color(0xFF171D2A),
    onBackground = Color(0xFFF4F7FB),
    onSurface = Color(0xFFF4F7FB)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF4656D9),
    secondary = Color(0xFF008F70),
    background = Color(0xFFF6F8FC),
    surface = Color.White,
    surfaceVariant = Color(0xFFE9EDF5)
)

@Composable
fun SignalBotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
