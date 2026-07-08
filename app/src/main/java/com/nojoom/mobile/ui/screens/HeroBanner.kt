package com.nojoom.mobile.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

private data class Star(val x: Float, val y: Float, val radius: Float, val phaseOffset: Float)
private data class Fish(val yFrac: Float, val scale: Float, val speed: Float, val startOffset: Float, val color: Color)

/**
 * Mirrors the desktop dashboard's luxury animated hero banner:
 * deep navy/blue gradient, night-sky stars, underwater wave layers,
 * and small fish silhouettes drifting across.
 */
@Composable
fun HeroBanner(title: String, subtitle: String, modifier: Modifier = Modifier) {
    val stars = remember {
        List(28) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat() * 0.55f,
                radius = Random.nextFloat() * 1.6f + 0.6f,
                phaseOffset = Random.nextFloat() * 6.28f
            )
        }
    }
    val fishes = remember {
        listOf(
            Fish(0.68f, 1.0f, 0.09f, 0f, Color(0xFFD4AF37)),
            Fish(0.78f, 0.7f, 0.13f, 0.4f, Color(0xFFF1D77E)),
            Fish(0.85f, 0.55f, 0.16f, 0.75f, Color(0xFFB8860B)),
        )
    }

    val infinite = rememberInfiniteTransition(label = "hero")
    val wavePhase by infinite.animateFloat(
        initialValue = 0f, targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "wave"
    )
    val twinkle by infinite.animateFloat(
        initialValue = 0f, targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "twinkle"
    )
    val fishProgress by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "fish"
    )

    Box(
        modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Deep navy/blue gradient background — night sky at top, underwater at bottom
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF060A18),
                        Color(0xFF0B1229),
                        Color(0xFF13234A)
                    )
                ),
                size = size
            )

            // Twinkling stars
            stars.forEach { star ->
                val alpha = (0.3f + 0.5f * ((sin(twinkle + star.phaseOffset) + 1f) / 2f)).coerceIn(0.15f, 0.9f)
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = star.radius,
                    center = Offset(star.x * w, star.y * h)
                )
            }

            // Underwater wave layers, back to front
            val waveLayers = listOf(
                Triple(0.72f, Color(0xFF1B3A6B).copy(alpha = 0.55f), 1.0f),
                Triple(0.80f, Color(0xFF1E4A85).copy(alpha = 0.5f), 1.4f),
                Triple(0.90f, Color(0xFFD4AF37).copy(alpha = 0.18f), 1.8f)
            )
            waveLayers.forEach { (baseYFrac, color, speedMult) ->
                val path = Path()
                val baseY = h * baseYFrac
                val amplitude = h * 0.035f
                path.moveTo(0f, h)
                path.lineTo(0f, baseY)
                var x = 0f
                val step = 8f
                while (x <= w) {
                    val y = baseY + amplitude * sin((x / w) * 4 * Math.PI + wavePhase * speedMult).toFloat()
                    path.lineTo(x, y)
                    x += step
                }
                path.lineTo(w, h)
                path.close()
                drawPath(path, color = color)
            }

            // Fish silhouettes drifting left to right, wrapping around
            fishes.forEach { fish ->
                val progress = (fishProgress * fish.speed * 10f + fish.startOffset) % 1.2f - 0.1f
                val fx = progress * (w + 60.dp.toPx()) - 30.dp.toPx()
                val fy = h * fish.yFrac + 6f * sin(wavePhase + fish.startOffset * 10f).toFloat()
                val s = fish.scale * 10.dp.toPx()

                val body = Path().apply {
                    moveTo(fx - s, fy)
                    quadraticTo(fx - s * 0.3f, fy - s * 0.6f, fx + s, fy)
                    quadraticTo(fx - s * 0.3f, fy + s * 0.6f, fx - s, fy)
                    close()
                }
                drawPath(body, color = fish.color.copy(alpha = 0.85f))
                // tail
                val tail = Path().apply {
                    moveTo(fx - s, fy)
                    lineTo(fx - s * 1.6f, fy - s * 0.45f)
                    lineTo(fx - s * 1.6f, fy + s * 0.45f)
                    close()
                }
                drawPath(tail, color = fish.color.copy(alpha = 0.7f))
            }
        }

        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFF1D77E),
                fontWeight = FontWeight.Bold
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFDDE1EA).copy(alpha = 0.85f)
            )
        }
    }
}
