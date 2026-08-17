package com.farhad.signalbot.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

@Composable
fun MarketChart(
    data: MarketChartData,
    modifier: Modifier = Modifier
) {

    var zoom by remember {
        mutableFloatStateOf(1f)
    }

    val bullish =
        MaterialTheme.colorScheme.primary

    val bearish =
        MaterialTheme.colorScheme.error

    val fast =
        MaterialTheme.colorScheme.tertiary

    val slow =
        MaterialTheme.colorScheme.secondary

    Card(
        modifier =
            modifier.fillMaxWidth(),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            )
    ) {

        Canvas(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(12.dp)
                    .pointerInput(Unit) {

                        detectTransformGestures {
                                _,
                                _,
                                scale,
                                _ ->

                            zoom =
                                (
                                    zoom *
                                        scale
                                    )
                                    .coerceIn(
                                        1f,
                                        4f
                                    )
                        }
                    }
        ) {

            val points =
                data.points

            if (
                points.size < 2
            ) {
                return@Canvas
            }

            val count =
                (
                    points.size /
                        zoom
                    )
                    .toInt()
                    .coerceIn(
                        2,
                        points.size
                    )

            val visible =
                points.takeLast(
                    count
                )

            val range =
                (
                    data.maxPrice -
                        data.minPrice
                    )
                    .coerceAtLeast(
                        0.0000001
                    )

            val width =
                size.width /
                    visible.size
                        .toFloat()

            fun mapY(
                value: Double
            ): Float {

                return (
                    size.height -
                        (
                            (
                                value -
                                    data.minPrice
                                ) /
                                range *
                                size.height
                            )
                            .toFloat()
                    )
            }

            visible.forEachIndexed {
                    index,
                    point ->

                val x =
                    index *
                        width +
                        width / 2f

                val high =
                    mapY(
                        point.high
                    )

                val low =
                    mapY(
                        point.low
                    )

                val open =
                    mapY(
                        point.open
                    )

                val close =
                    mapY(
                        point.close
                    )

                val candleColor =
                    if (
                        point.close >=
                            point.open
                    ) {
                        bullish
                    } else {
                        bearish
                    }

                drawLine(
                    color =
                        candleColor,

                    start =
                        Offset(
                            x,
                            high
                        ),

                    end =
                        Offset(
                            x,
                            low
                        ),

                    strokeWidth =
                        1.5f
                )

                val top =
                    min(
                        open,
                        close
                    )

                val bottom =
                    max(
                        open,
                        close
                    )

                drawRect(
                    color =
                        candleColor,

                    topLeft =
                        Offset(
                            x -
                                width *
                                0.32f,

                            top
                        ),

                    size =
                        Size(
                            width =
                                width *
                                0.64f,

                            height =
                                max(
                                    bottom -
                                        top,

                                    2f
                                )
                        ),

                    alpha =
                        0.88f
                )
            }

            drawIndicator(
                points =
                    visible,

                selector = {
                    it.emaFast
                },

                mapper =
                    ::mapY,

                color =
                    fast
            )

            drawIndicator(
                points =
                    visible,

                selector = {
                    it.emaSlow
                },

                mapper =
                    ::mapY,

                color =
                    slow
            )
        }
    }
}

private fun DrawScope.drawIndicator(
    points: List<ChartPoint>,
    selector:
        (ChartPoint) -> Double?,
    mapper:
        (Double) -> Float,
    color: Color
) {

    val path =
        Path()

    var started =
        false

    val width =
        size.width /
            points.size
                .toFloat()

    points.forEachIndexed {
            index,
            point ->

        val value =
            selector(point)
                ?: return@forEachIndexed

        val x =
            index *
                width +
                width / 2f

        val y =
            mapper(value)

        if (!started) {

            path.moveTo(
                x,
                y
            )

            started = true

        } else {

            path.lineTo(
                x,
                y
            )
        }
    }

    if (started) {

        drawPath(
            path =
                path,

            color =
                color,

            style =
                Stroke(
                    width =
                        2.5f
                )
        )
    }
}
