package com.pixel.composeexperiments

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.MeshGradientPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 2 US gallons. Note this is a very high target - see the note in the readme-level
 * comment below. Change this one value to retarget the whole screen.
 */
private const val GOAL_ML = 7570

/** The amounts the three buttons add. */
private val POUR_SIZES = listOf(100, 200, 300)

private val EmptyTank = Color(0xFF06122E)

/**
 * A wrapping 0f..1f phase ramp - one per surface, not one per column.
 *
 * It has to be linear and it has to feed sin/cos, because the whole thing rests on
 * sin^2 + cos^2 = 1: that identity is what holds the wave's amplitude constant while
 * only its shape rotates. Substituting eased tweens for the sine pair (tried, then
 * measured) makes that sum swing instead, so the surface visibly flattens out at two
 * points in every cycle and re-forms - a standing wave, not a travelling one.
 */
@Composable
private fun InfiniteTransition.phase(period: Int, label: String) = animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
        animation = tween(period, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    ),
    label = label
)

// The two spatial shapes a travelling wave decomposes into, sampled once per column
// from a wave 3.2 screens long - one column every 22.5 degrees. These are just
// cos(kx) and sin(kx) worked out ahead of time, so nothing computes a sine at
// runtime, but the pair still reconstructs a crest that MOVES rather than one shape
// swelling in place.
//
// column:                         0       1       2       3       4       5
private val LEAD = floatArrayOf(1.000f, 0.924f, 0.707f, 0.383f, 0.000f, -0.383f)
private val LAG = floatArrayOf(0.000f, 0.383f, 0.707f, 0.924f, 1.000f, 0.924f)

@Composable
fun WaterTrackerMeshGradient(modifier: Modifier = Modifier) {
    var consumedMl by rememberSaveable { mutableIntStateOf(0) }

    val progress = (consumedMl.toFloat() / GOAL_ML).coerceIn(0f, 1f)

    // Spring rather than a tween so the level overshoots slightly and settles -
    // that settle is what makes a pour read as liquid rather than a progress bar.
    val fill by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = Spring.StiffnessLow
        ),
        label = "fill"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EmptyTank)
    ) {
        // The water itself, full-bleed and drawn behind everything else. Its height
        // is the fill level; the mesh's wavy top row becomes the water surface.
        // The spring can overshoot past 0..1, and fillMaxHeight rejects that, so the
        // fraction is coerced here rather than clamping the animation.
        if (fill > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(fill.coerceIn(0.001f, 1f))
                    .paint(rememberWaterPainter())
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "Today",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${formatLitres(consumedMl, decimals = 2)} L",
                color = Color.White,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "of ${formatLitres(GOAL_ML, decimals = 1)} L goal  ·  ${(progress * 100).toInt()}%",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                POUR_SIZES.forEach { amount ->
                    Button(
                        onClick = { consumedMl += amount },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White.copy(alpha = 0.18f),
                            contentColor = Color.White
                        )
                    ) {
                        Text("+$amount ml")
                    }
                }
            }

            TextButton(onClick = { consumedMl = 0 }) {
                Text("Reset", color = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}

/**
 * `String.format` is JVM-only, so the litre readout is assembled from integer maths
 * instead - the same string on Android, iOS, desktop and web.
 */
private fun formatLitres(millilitres: Int, decimals: Int): String {
    val unitsPerLitre = if (decimals == 1) 10 else 100
    val mlPerUnit = 1000 / unitsPerLitre
    val units = (millilitres + mlPerUnit / 2) / mlPerUnit
    val whole = units / unitsPerLitre
    val fraction = (units % unitsPerLitre).toString().padStart(decimals, '0')
    return "$whole.$fraction"
}

/**
 * The water body. Row 0 is the surface and carries the wave; row 1 trails it so the
 * colour bends just after the crest passes. Rows 2-5 are static and anchored to the
 * bottom, which is what makes the body below the surface feel like settled water.
 */
@Composable
private fun rememberWaterPainter(): MeshGradientPainter {
    // One animation for the whole surface. sin/cos of it are the two time terms; the
    // per-column constants below are the two space terms.
    val infiniteTransition = rememberInfiniteTransition(label = "surface")
    val phase by infiniteTransition.phase(period = 3600, label = "phase")

    // How far the surface travels. Drive this from how recently the user poured to
    // make the water slosh on each tap; 0f gives a dead-flat surface.
    val amplitude = 1f

    val rows = 5
    val columns = 5

    // Light at the surface, deep at the bottom - the way water actually reads.
    val foam = Color(0xFF9BF0E8)
    val aqua = Color(0xFF4FD6E8)
    val shallow = Color(0xFF2AB0E0)
    val mid = Color(0xFF1C86D4)
    val deep = Color(0xFF1355B8)
    val abyss = Color(0xFF0B2E86)

    return remember(rows, columns) {
        MeshGradientPainter(rows, columns, hasBicubicColor = true) {
            // sin(wt - kx) = sin(wt)cos(kx) - cos(wt)sin(kx). Two trig calls per
            // frame, not per vertex - the per-column halves are the LEAD/LAG
            // constants. The minus sign sets which way the crest travels.
            val wt = phase * 2f * PI.toFloat()
            val sinWt = sin(wt)
            val cosWt = cos(wt)

            fun surface(column: Int) = LEAD[column] * sinWt - LAG[column] * cosWt

            val crest = 0.04f * amplitude
            val trail = 0.016f * amplitude
            val sway = 0.015f * amplitude

            // Row 0 - the surface. The corners are pinned in x only: holding them at
            // x = 0 and x = 1 keeps the water spanning the full width, while their y
            // stays free so the shape carries through the ends instead of dying into
            // a fixed point.
            setVertex(0, 0, Offset(0.0f, 0.14f + crest * surface(0)), foam)
            setVertex(0, 1, Offset(0.2f + sway * surface(1), 0.14f + crest * surface(1)), foam)
            setVertex(0, 2, Offset(0.4f + sway * surface(2), 0.14f + crest * surface(2)), aqua)
            setVertex(0, 3, Offset(0.6f + sway * surface(3), 0.14f + crest * surface(3)), aqua)
            setVertex(0, 4, Offset(0.8f + sway * surface(4), 0.14f + crest * surface(4)), foam)
            setVertex(0, 5, Offset(1.0f, 0.14f + crest * surface(5)), aqua)

            // Row 1 - the same shape at well under half the travel, so the colour band
            // bends with the surface without looking like a second wave.
            setVertex(1, 0, Offset(0.0f, 0.34f + trail * surface(0)), aqua)
            setVertex(1, 1, Offset(0.2f + sway * surface(1), 0.34f + trail * surface(1)), shallow)
            setVertex(1, 2, Offset(0.4f + sway * surface(2), 0.34f + trail * surface(2)), aqua)
            setVertex(1, 3, Offset(0.6f + sway * surface(3), 0.34f + trail * surface(3)), shallow)
            setVertex(1, 4, Offset(0.8f + sway * surface(4), 0.34f + trail * surface(4)), shallow)
            setVertex(1, 5, Offset(1.0f, 0.34f + trail * surface(5)), aqua)

            // Rows 2-5 - static, anchored to the bottom, darkening with depth.
            setVertex(2, 0, Offset(0.0f, 0.52f), shallow)
            setVertex(2, 1, Offset(0.2f, 0.52f), shallow)
            setVertex(2, 2, Offset(0.4f, 0.52f), mid)
            setVertex(2, 3, Offset(0.6f, 0.52f), shallow)
            setVertex(2, 4, Offset(0.8f, 0.52f), mid)
            setVertex(2, 5, Offset(1.0f, 0.52f), mid)

            setVertex(3, 0, Offset(0.0f, 0.68f), mid)
            setVertex(3, 1, Offset(0.2f, 0.68f), mid)
            setVertex(3, 2, Offset(0.4f, 0.68f), deep)
            setVertex(3, 3, Offset(0.6f, 0.68f), mid)
            setVertex(3, 4, Offset(0.8f, 0.68f), deep)
            setVertex(3, 5, Offset(1.0f, 0.68f), deep)

            setVertex(4, 0, Offset(0.0f, 0.84f), deep)
            setVertex(4, 1, Offset(0.2f, 0.84f), deep)
            setVertex(4, 2, Offset(0.4f, 0.84f), abyss)
            setVertex(4, 3, Offset(0.6f, 0.84f), deep)
            setVertex(4, 4, Offset(0.8f, 0.84f), abyss)
            setVertex(4, 5, Offset(1.0f, 0.84f), abyss)

            setVertex(5, 0, Offset(0.0f, 1.0f), abyss)
            setVertex(5, 1, Offset(0.2f, 1.0f), abyss)
            setVertex(5, 2, Offset(0.4f, 1.0f), abyss)
            setVertex(5, 3, Offset(0.6f, 1.0f), abyss)
            setVertex(5, 4, Offset(0.8f, 1.0f), abyss)
            setVertex(5, 5, Offset(1.0f, 1.0f), abyss)
        }
    }
}
