package com.pixel.composeexperiments

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pixel.composeexperiments.libby.LibbyBookArrangement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("home")
data object Home

@Serializable
@SerialName("libby_text_arrangement")
object LibbyTextArrangement

@Serializable
@SerialName("text_animation")
data object TextAnim

@Serializable
@SerialName("lyft_button_shadow")
data object LyftButton

@Serializable
@SerialName("water_mesh_gradient")
data object WaterTracker

private const val RepoUrl = "https://github.com/kevindesai777/compose-experiments"

// Same palette as the app icon: navy-to-teal diagonal under a faint green lab grid.
private val Navy = Color(0xFF0B2E42)
private val Teal = Color(0xFF0E5049)
private val GridLine = Color(0xFF3DDC84).copy(alpha = 0.08f)
private val Accent = Color(0xFF3DDC84)
private val Tagline = Color(0xFF8FD9B6)
private val Muted = Color(0xFF8FB3C4)

@Composable
fun App(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            HomeScreen(
                experiments = listOf(
                    "Libby Book Arrangement" to { navController.navigate(LibbyTextArrangement) },
                    "Severance Text Animation" to { navController.navigate(TextAnim) },
                    "Lyft Shadow Button" to { navController.navigate(LyftButton) },
                    "Water Tracker Mesh Gradient" to { navController.navigate(WaterTracker) },
                )
            )
        }
        composable<LibbyTextArrangement> { LibbyBookArrangement() }
        composable<TextAnim> { HelloMsCobelTextAnimation() }
        composable<LyftButton> { LyftButtonShadow() }
        composable<WaterTracker> { WaterTrackerMeshGradient() }
    }

}

@Composable
fun HomeScreen(experiments: List<Pair<String, () -> Unit>>) {
    val uriHandler = LocalUriHandler.current
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(Navy, Teal)))
                .drawBehind {
                    val step = 28.dp.toPx()
                    val stroke = 1.dp.toPx()
                    var x = step
                    while (x < size.width) {
                        drawLine(GridLine, Offset(x, 0f), Offset(x, size.height), stroke)
                        x += step
                    }
                    var y = step
                    while (y < size.height) {
                        drawLine(GridLine, Offset(0f, y), Offset(size.width, y), stroke)
                        y += step
                    }
                }
                .safeDrawingPadding()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.widthIn(max = 420.dp).fillMaxSize(),
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Compose Experiments",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                )
                Text(
                    text = "UI experiments built with Compose Multiplatform",
                    color = Tagline,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 6.dp, bottom = 36.dp),
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    experiments.forEach { (title, onClick) ->
                        ExperimentButton(title = title, onClick = onClick)
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "View source on GitHub",
                    color = Muted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(50))
                        .clickable(role = Role.Button) { uriHandler.openUri(RepoUrl) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun ExperimentButton(title: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF06121E).copy(alpha = 0.55f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), shape)
            .clickable(role = Role.Button, onClick = onClick)
            .heightIn(min = 60.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f).padding(end = 12.dp),
        )
        // Drawn rather than a "›" glyph, which the default web font may not have.
        Canvas(Modifier.size(width = 8.dp, height = 14.dp)) {
            val stroke = 2.dp.toPx()
            drawLine(Accent, Offset(0f, 0f), Offset(size.width, size.height / 2), stroke, StrokeCap.Round)
            drawLine(Accent, Offset(size.width, size.height / 2), Offset(0f, size.height), stroke, StrokeCap.Round)
        }
    }
}
