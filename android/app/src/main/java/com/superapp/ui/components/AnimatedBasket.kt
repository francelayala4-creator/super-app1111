package com.superapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedBasketLoading(
    label: String = "Comparando precios",
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
) {
    val infinite = rememberInfiniteTransition(label = "basket")
    val tick by infinite.animateFloat(
        initialValue = 0f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(3600, easing = LinearEasing), RepeatMode.Restart),
        label = "tick"
    )
    val primary = MaterialTheme.colorScheme.primary
    val accent = MaterialTheme.colorScheme.tertiary
    val coral = MaterialTheme.colorScheme.error
    val teal = MaterialTheme.colorScheme.secondary

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(size)) {
            Canvas(Modifier.fillMaxSize()) {
                val w = this.size.width
                val h = this.size.height
                val cx = w / 2f
                val basketTop = h * 0.55f
                val basketBottom = h * 0.92f
                val basketW = w * 0.78f
                val basketLeft = cx - basketW / 2f

                // Items que caen y se acumulan
                val palette = listOf(primary, accent, coral, teal, accent, primary)
                palette.forEachIndexed { idx, color ->
                    val phase = (tick + idx * 0.55f) % 6f
                    val r = w * 0.055f
                    val targetXOffset = ((idx % 3) - 1) * basketW * 0.22f
                    when {
                        phase < 1.2f -> {
                            // cayendo
                            val t = phase / 1.2f
                            val y = h * 0.05f + (basketTop + h * 0.05f - h * 0.05f) * t
                            drawCircle(color, r, Offset(cx + targetXOffset, y))
                        }
                        else -> {
                            // dentro de la canasta (stacked)
                            val stackRow = (idx / 3)
                            val y = basketBottom - r * 1.2f - stackRow * r * 1.4f
                            drawCircle(color, r, Offset(cx + targetXOffset, y))
                        }
                    }
                }

                // Canasta (trapezoide invertido)
                val basketPath = Path().apply {
                    moveTo(basketLeft, basketTop)
                    lineTo(basketLeft + basketW, basketTop)
                    lineTo(basketLeft + basketW * 0.88f, basketBottom)
                    lineTo(basketLeft + basketW * 0.12f, basketBottom)
                    close()
                }
                drawPath(basketPath, color = primary, style = Stroke(width = w * 0.020f, cap = StrokeCap.Round, join = StrokeJoin.Round))

                // Mesh canasta
                listOf(0.25f, 0.50f, 0.75f).forEach { f ->
                    val x = basketLeft + basketW * f
                    drawLine(
                        primary.copy(alpha = 0.45f),
                        start = Offset(x, basketTop + (basketBottom - basketTop) * 0.10f),
                        end = Offset(x, basketBottom * 0.98f),
                        strokeWidth = w * 0.009f,
                    )
                }
                listOf(0.35f, 0.70f).forEach { f ->
                    val y = basketTop + (basketBottom - basketTop) * f
                    drawLine(
                        primary.copy(alpha = 0.45f),
                        start = Offset(basketLeft + basketW * 0.13f, y),
                        end = Offset(basketLeft + basketW * 0.87f, y),
                        strokeWidth = w * 0.009f,
                    )
                }
                // Borde superior elipse (efecto 3D)
                drawOval(
                    color = primary,
                    topLeft = Offset(basketLeft, basketTop - w * 0.025f),
                    size = androidx.compose.ui.geometry.Size(basketW, w * 0.05f),
                    style = Stroke(width = w * 0.020f),
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
    }
}
