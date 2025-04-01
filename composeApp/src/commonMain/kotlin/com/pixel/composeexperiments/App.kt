package com.pixel.composeexperiments

import LibbyBookArrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview


@Serializable
object Home

@Serializable
object LibbyTextArrangement

@Serializable
object TextAnim

@Composable
fun App(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            HomeScreen(
                onNavigateToTextAnim = {
                    navController.navigate(TextAnim)
                },
                onNavigateToBookUI = {
                    navController.navigate(LibbyTextArrangement)
                }
            )
        }
        composable<LibbyTextArrangement> { LibbyBookArrangement() }
        composable<TextAnim> { HelloMsCobelTextAnimation() }
    }

}

@Composable
fun HomeScreen(
    onNavigateToBookUI: () -> Unit, onNavigateToTextAnim: () -> Unit
) {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFF0F2027),
                            Color(0xFF203A43),
                            Color(0xFF2C5364)
                        )
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        onNavigateToBookUI()
                    },
                ) {
                    Text("Libby Book Arrangement")
                }

                Button(
                    onClick = {
                        onNavigateToTextAnim()
                    },
                ) {
                    Text("Severance Text Animation")
                }
            }
        }
    }
}
