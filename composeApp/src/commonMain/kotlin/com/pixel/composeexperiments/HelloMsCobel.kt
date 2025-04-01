package com.pixel.composeexperiments

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

@Composable
fun HelloMsCobelTextAnimation() {

    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 4f,
        animationSpec =
            infiniteRepeatable(
                // Infinitely repeating a 1000ms tween animation using default easing curve.
                animation = tween(2000),
                // After each iteration of the animation (i.e. every 1000ms), the animation
                // will
                // start again from the [initialValue] defined above.
                // This is the default [RepeatMode]. See [RepeatMode.Reverse] below for an
                // alternative.
                repeatMode = RepeatMode.Restart
            )
    )

    val translate by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2000f,
        animationSpec =
            infiniteRepeatable(
                // Infinitely repeating a 1000ms tween animation using default easing curve.
                animation = tween(1000, delayMillis = 500),
                // After each iteration of the animation (i.e. every 1000ms), the animation
                // will
                // start again from the [initialValue] defined above.
                // This is the default [RepeatMode]. See [RepeatMode.Reverse] below for an
                // alternative.
                repeatMode = RepeatMode.Restart
            )
    )

    Box(modifier = Modifier.fillMaxSize()
        .background(Color.Black),
        contentAlignment = Alignment.Center) {
        Text(
            text = "Hello Ms. Cobel",
            color = Color.Cyan,
            fontSize = TextUnit(64f, type = TextUnitType.Sp),
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    translationX = -translate
                    transformOrigin = TransformOrigin(1f, 0.5f)
                }
        )
    }
}