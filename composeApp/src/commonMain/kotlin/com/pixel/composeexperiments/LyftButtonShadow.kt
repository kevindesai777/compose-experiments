package com.pixel.composeexperiments

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LyftButtonShadow() {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val floatVal by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val gradientBrush = Brush.linearGradient(
        listOf(Color.Green, Color.Green, Color.Yellow, Color.Yellow),
        stops = listOf(0f, 0.35f, 0.65f, 1f),
        angleInDegrees = floatVal
    )
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.Black)
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .dropShadow(
                    shape = RoundedCornerShape(50)
                ) {
                    this.brush = gradientBrush
                    this.radius = 48f
                    this.spread = 16f
                }
                .background(gradientBrush, shape = RoundedCornerShape(50)),
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
            onClick = {},
        ) {
            Text("Lyft", color = Color.Black, fontSize = 36.sp) //TODO: can put custom font
        }
    }
}

@Preview
@Composable
fun LyftPreview() {
    LyftButtonShadow()
}
