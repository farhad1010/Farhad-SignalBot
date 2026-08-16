package com.farhad.signalbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Background = Color(0xFF070A0F)
private val Panel = Color(0xFF111820)
private val Panel2 = Color(0xFF151E28)
private val Green = Color(0xFF00F59B)
private val GreenDark = Color(0xFF00B875)
private val Red = Color(0xFFFF263D)
private val White = Color(0xFFF4F7FA)
private val Muted = Color(0xFF8C98A7)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                SignalBotApp()
            }
        }
    }
}

@Composable
fun SignalBotApp() {

    var scanning by remember { mutableStateOf(false) }
    var signal by remember { mutableStateOf("DOWN") }
    var seconds by remember { mutableIntStateOf(60) }

    LaunchedEffect(scanning) {
        if (scanning) {

            seconds = 60

            delay(2600)

            signal = if ((0..1).random() == 0) "DOWN" else "UP"
            scanning = false

            while (seconds > 0) {
                delay(1000)
                seconds--
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {

            TopBar()

            Spacer(modifier = Modifier.height(12.dp))

            PairRow()

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                TradingChart()

                SignalPanel(
                    scanning = scanning,
                    signal = signal,
                    seconds = seconds,
                    onScan = {
                        if (!scanning) {
                            scanning = true
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            BottomInfo()

        }
    }
}

@Composable
private fun TopBar() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(18.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Green.copy(alpha = 0.38f),
                            Color(0xFF102B24)
                        )
                    )
                )
                .border(
                    1.dp,
                    Green.copy(alpha = 0.8f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "TR",
                color = Green,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "FARHAD SIGNALBOT",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "AI MARKET ANALYSIS",
                color = Green,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Panel2),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "47",
                color = Red,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PairRow() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        PairCard(
            pair = "EUR/USD",
            percent = "92%",
            modifier = Modifier.weight(1f)
        )

        PairCard(
            pair = "GBP/JPY",
            percent = "92%",
            modifier = Modifier.weight(1f)
        )

        PairCard(
            pair = "USD/JPY",
            percent = "89%",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PairCard(
    pair: String,
    percent: String,
    modifier: Modifier
) {

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(Panel2)
            .border(
                1.dp,
                Color.White.copy(alpha = 0.07f),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "$pair (OTC)",
            color = White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = percent,
            color = Color(0xFFFFA32B),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun TradingChart() {

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF111923))
    ) {

        val width = size.width
        val height = size.height

        val gridColor = Color(0xFF26333F)

        for (x in 0..8) {

            val xPos = width * x / 8f

            drawLine(
                color = gridColor,
                start = androidx.compose.ui.geometry.Offset(xPos, 0f),
                end = androidx.compose.ui.geometry.Offset(xPos, height),
                strokeWidth = 1f
            )
        }

        for (y in 0..10) {

            val yPos = height * y / 10f

            drawLine(
                color = gridColor,
                start = androidx.compose.ui.geometry.Offset(0f, yPos),
                end = androidx.compose.ui.geometry.Offset(width, yPos),
                strokeWidth = 1f
            )
        }

        drawLine(
            color = Color(0xFF00D7C7),
            start = androidx.compose.ui.geometry.Offset(0f, height * .54f),
            end = androidx.compose.ui.geometry.Offset(width, height * .54f),
            strokeWidth = 2f
        )

        drawLine(
            color = Color(0xFFFFB52E),
            start = androidx.compose.ui.geometry.Offset(0f, height * .70f),
            end = androidx.compose.ui.geometry.Offset(width, height * .70f),
            strokeWidth = 2f
        )

        val candles = listOf(
            Triple(.20f, .58f, .37f),
            Triple(.30f, .49f, .28f),
            Triple(.40f, .60f, .43f),
            Triple(.50f, .42f, .26f),
            Triple(.60f, .53f, .34f),
            Triple(.70f, .36f, .20f)
        )

        candles.forEachIndexed { index, candle ->

            val x = width * candle.first
            val open = height * candle.second
            val close = height * candle.third

            val bullish = index % 3 != 1
            val candleColor = if (bullish) Green else Red

            drawLine(
                color = candleColor,
                start = androidx.compose.ui.geometry.Offset(x, open - 40f),
                end = androidx.compose.ui.geometry.Offset(x, close + 40f),
                strokeWidth = 5f
            )

            drawRect(
                color = candleColor,
                topLeft = androidx.compose.ui.geometry.Offset(
                    x - 17f,
                    minOf(open, close)
                ),
                size = androidx.compose.ui.geometry.Size(
                    34f,
                    kotlin.math.abs(close - open).coerceAtLeast(45f)
                )
            )
        }
    }
}

@Composable
private fun SignalPanel(
    scanning: Boolean,
    signal: String,
    seconds: Int,
    onScan: () -> Unit
) {

    val infinite = rememberInfiniteTransition(label = "glow")

    val glow by infinite.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(.72f)
            .padding(top = 8.dp, bottom = 8.dp)
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(22.dp)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF18232C),
                        Color(0xFF0E151C)
                    )
                )
            )
            .border(
                width = 2.dp,
                color = Green.copy(alpha = glow),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(22.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "TR BOT",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF263B4B)),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "×",
                        color = White,
                        fontSize = 25.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(38.dp))

            Text(
                text = if (scanning) "ANALYZING MARKET" else "SIGNAL READY",
                color = White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            ScanIndicator(
                scanning = scanning,
                glow = glow
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (scanning) "SCANNING..." else "SCAN COMPLETE",
                color = Green,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "PREMIUM SIGNAL",
                color = Color(0xFFFFD0D0),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (scanning) "—" else signal,
                color = if (signal == "DOWN") Red else Green,
                fontSize = 46.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Trader Mode",
                color = White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "@FarhadSignal",
                color = Green,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "EXPIRY",
                color = Muted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Text(
                text = String.format("00:%02d", seconds),
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        if (scanning)
                            Color(0xFF26313A)
                        else
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF00C77A),
                                    Color(0xFF00F59B)
                                )
                            )
                    )
                    .clickable(enabled = !scanning) {
                        onScan()
                    },
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = if (scanning) "SCANNING..." else "SCAN MARKET",
                    color = if (scanning) Muted else Color(0xFF04120C),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
private fun ScanIndicator(
    scanning: Boolean,
    glow: Float
) {

    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            drawCircle(
                color = Green.copy(alpha = .12f),
                radius = size.minDimension / 2
            )

            drawCircle(
                color = Green.copy(alpha = glow),
                radius = size.minDimension / 2 - 7.dp.toPx(),
                style = Stroke(
                    width = 2.dp.toPx()
                )
            )

            drawCircle(
                color = Green.copy(alpha = .7f),
                radius = size.minDimension / 2 - 17.dp.toPx(),
                style = Stroke(
                    width = 2.dp.toPx()
                )
            )

            drawLine(
                color = Green,
                start = androidx.compose.ui.geometry.Offset(
                    size.width / 2,
                    size.height / 2
                ),
                end = androidx.compose.ui.geometry.Offset(
                    if (scanning) size.width - 12.dp.toPx()
                    else size.width - 24.dp.toPx(),
                    size.height / 2
                ),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawCircle(
                color = Green,
                radius = 5.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(
                    size.width / 2,
                    size.height / 2
                )
            )
        }
    }
}

@Composable
private fun BottomInfo() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Panel)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "LIVE MARKET",
            color = Green,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "OTC",
            color = Muted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = "AI ENGINE READY",
            color = White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
