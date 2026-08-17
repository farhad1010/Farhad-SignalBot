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

    val bullishColor =
        MaterialTheme.colorScheme.primary

    val bearishColor =
        MaterialTheme.colorScheme.error

    val emaFastColor =
        MaterialTheme.colorScheme.tertiary

    val emaSlowColor =
        MaterialTheme.colorScheme.secondary

    Card(
        modifier =
            modifier.fillMaxWidth(),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme
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
                                gestureZoom,
                                _ ->

                            zoom =
                                (
                                    zoom *
                                        gestureZoom
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

            if (points.size < 2) {
                return@Canvas
            }

            val visibleCount =
                max(
                    20,
                    (
                        points.size / zoom
                        ).toInt()
                )
                    .coerceAtMost(
                        points.size
                    )

            val visible =
                points.takeLast(
                    visibleCount
                )

            val priceRange =
                max(
                    data.maxPrice -
                        data.minPrice,
                    0.0000001
                )

            val candleWidth =
                size.width /
                    visible.size
                        .toFloat()

            fun y(
                price: Double
            ): Float {

                return (
                    size.height -
                        (
                            (
                                price -
                                    data.minPrice
                                ) /
                                priceRange *
                                size.height
                            ).toFloat()
                    )
            }

            visible.forEachIndexed {
                    index,
                    point ->

                val centerX =
                    index *
                        candleWidth +
                        candleWidth / 2f

                val highY =
                    y(point.high)

                val lowY =
                    y(point.low)

                val openY =
                    y(point.open)

                val closeY =
                    y(point.close)

                val candleColor =
                    if (
                        point.close >=
                            point.open
                    ) {
                        bullishColor
                    } else {
                        bearishColor
                    }

                /*
                 * Candle wick
                 */
                drawLine(
                    color = candleColor,
                    start =
                        Offset(
                            centerX,
                            highY
                        ),
                    end =
                        Offset(
                            centerX,
                            lowY
                        ),
                    strokeWidth = 1.5f
                )

                val bodyTop =
                    min(
                        openY,
                        closeY
                    )

                val bodyBottom =
                    max(
                        openY,
                        closeY
                    )

                /*
                 * Candle body
                 */
                drawRect(
                    color = candleColor,
                    topLeft =
                        Offset(
                            centerX -
                                candleWidth *
                                0.32f,
                            bodyTop
                        ),
                    size =
                        Size(
                            width =
                                candleWidth *
                                    0.64f,
                            height =
                                max(
                                    bodyBottom -
                                        bodyTop,
                                    2f
                                )
                        ),
                    alpha = 0.88f
                )
            }

            /*
             * EMA 12
             */
            drawIndicatorLine(
                points = visible,
                selector = {
                    it.emaFast
                },
                yMapper = ::y,
                strokeWidth = 2.5f,
                color = emaFastColor
            )

            /*
             * EMA 26
             */
            drawIndicatorLine(
                points = visible,
                selector = {
                    it.emaSlow
                },
                yMapper = ::y,
                strokeWidth = 2.5f,
                color = emaSlowColor
            )
        }
    }
}

private fun DrawScope.drawIndicatorLine(
    points: List<ChartPoint>,
    selector: (ChartPoint) -> Double?,
    yMapper: (Double) -> Float,
    strokeWidth: Float,
    color: Color
) {

    val path =
        Path()

    var started =
        false

    val pointWidth =
        size.width /
            points.size.toFloat()

    points.forEachIndexed {
            index,
            point ->

        val value =
            selector(point)
                ?: return@forEachIndexed

        val x =
            index *
                pointWidth +
                pointWidth / 2f

        val y =
            yMapper(value)

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
            path = path,
            color = color,
            style =
                Stroke(
                    width =
                        strokeWidth
                )
        )
    }
}
