package com.superapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

/**
 * Carrito ilustrado en movimiento, con items cayendo dentro.
 * Se anima con un loop infinito: el carrito se balancea ligeramente y cae un producto cada beat.
 */
@Composable
fun AnimatedCartHero(
    modifier: Modifier = Modifier,
    primary: Color = Color.White,
    accent: Color = Color(0xFFF0CC7A),
    size: Dp = 220.dp,
) {
    val infinite = rememberInfiniteTransition(label = "cart")
    val sway by infinite.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "sway"
    )
    val drop by infinite.animateFloat(
        initialValue = 0f, targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Restart),
        label = "drop"
    )

    Box(modifier.size(size)) {
        Canvas(Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h * 0.62f

            // Productos cayendo: 3 items en fases distintas
            val itemPhases = listOf(0f, 1.4f, 2.8f)
            itemPhases.forEachIndexed { i, phase ->
                val t = ((drop + phase) % 4f) / 4f
                if (t < 0.85f) {
                    val y = h * 0.05f + (cy - h * 0.18f) * (t / 0.85f)
                    val itemColor = listOf(accent, primary, Color(0xFFFF7A66))[i]
                    drawCircle(
                        color = itemColor.copy(alpha = 1f - t * 0.4f),
                        radius = w * 0.045f,
                        center = Offset(cx + (i - 1) * w * 0.10f, y),
                    )
                }
            }

            translate(left = sway * w * 0.012f) {
                // Cuerpo del carrito (trapezoide)
                val cartW = w * 0.62f
                val cartH = h * 0.30f
                val left = cx - cartW / 2f
                val top = cy - cartH / 2f
                val path = Path().apply {
                    moveTo(left, top)
                    lineTo(left + cartW, top)
                    lineTo(left + cartW * 0.88f, top + cartH)
                    lineTo(left + cartW * 0.12f, top + cartH)
                    close()
                }
                drawPath(path, color = primary, style = Stroke(width = w * 0.018f, cap = StrokeCap.Round, join = StrokeJoin.Round))

                // Líneas verticales (mesh del carrito)
                listOf(0.30f, 0.50f, 0.70f).forEach { f ->
                    val x = left + cartW * f
                    drawLine(
                        primary.copy(alpha = 0.55f),
                        start = Offset(x, top + cartH * 0.10f),
                        end = Offset(x, top + cartH * 0.90f),
                        strokeWidth = w * 0.008f,
                        cap = StrokeCap.Round,
                    )
                }
                // Líneas horizontales
                listOf(0.35f, 0.65f).forEach { f ->
                    val y = top + cartH * f
                    drawLine(
                        primary.copy(alpha = 0.55f),
                        start = Offset(left + cartW * 0.10f, y),
                        end = Offset(left + cartW * 0.90f, y),
                        strokeWidth = w * 0.008f,
                        cap = StrokeCap.Round,
                    )
                }

                // Mango
                drawLine(
                    primary,
                    start = Offset(left + cartW * 0.95f, top),
                    end = Offset(left + cartW * 1.05f, top - cartH * 0.40f),
                    strokeWidth = w * 0.020f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    primary,
                    start = Offset(left + cartW * 1.05f, top - cartH * 0.40f),
                    end = Offset(left + cartW * 1.15f, top - cartH * 0.40f),
                    strokeWidth = w * 0.020f,
                    cap = StrokeCap.Round,
                )

                // Ruedas
                val wheelY = cy + cartH * 0.65f
                val wheelR = w * 0.045f
                val wheelSpin = sin(sway * PI.toFloat()) * 0.3f
                drawCircle(primary, wheelR, Offset(left + cartW * 0.25f, wheelY))
                drawCircle(accent, wheelR * 0.4f, Offset(
                    left + cartW * 0.25f + wheelR * 0.25f * sin(wheelSpin),
                    wheelY,
                ))
                drawCircle(primary, wheelR, Offset(left + cartW * 0.75f, wheelY))
                drawCircle(accent, wheelR * 0.4f, Offset(
                    left + cartW * 0.75f + wheelR * 0.25f * sin(wheelSpin + PI.toFloat()),
                    wheelY,
                ))
            }
        }
    }
}
