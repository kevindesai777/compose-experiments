package com.pixel.composeexperiments.libby

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import composeexperiments.composeapp.generated.resources.Res
import composeexperiments.composeapp.generated.resources.book_alice
import composeexperiments.composeapp.generated.resources.book_anne
import composeexperiments.composeapp.generated.resources.book_black_beauty
import composeexperiments.composeapp.generated.resources.book_dracula
import composeexperiments.composeapp.generated.resources.book_eighty_days
import composeexperiments.composeapp.generated.resources.book_hound
import composeexperiments.composeapp.generated.resources.book_huck_finn
import composeexperiments.composeapp.generated.resources.book_invisible_man
import composeexperiments.composeapp.generated.resources.book_jungle_book
import composeexperiments.composeapp.generated.resources.book_land_of_oz
import composeexperiments.composeapp.generated.resources.book_peter_pan
import composeexperiments.composeapp.generated.resources.book_peter_rabbit
import composeexperiments.composeapp.generated.resources.book_rebecca
import composeexperiments.composeapp.generated.resources.book_secret_garden
import composeexperiments.composeapp.generated.resources.book_sherlock
import composeexperiments.composeapp.generated.resources.book_thirty_nine_steps
import composeexperiments.composeapp.generated.resources.book_time_machine
import composeexperiments.composeapp.generated.resources.book_tom_sawyer
import composeexperiments.composeapp.generated.resources.book_war_of_the_worlds
import composeexperiments.composeapp.generated.resources.book_wind_willows
import composeexperiments.composeapp.generated.resources.book_wizard_of_oz
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val CellWidth = 84.dp
private val CellHeight = 126.dp
private val Gap = 17.dp

/**
 * The cell is the covers' own 2:3 shape, so each cover fills it exactly — no
 * crop, no mat. Proportions are taken from Libby's site: the channel between
 * covers is a fifth of a cover's width. Like the app, the wall runs off every
 * edge of the screen, repeating covers to fill it.
 */
@Composable
fun LibbyBookArrangement() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clipToBounds(),
        contentAlignment = Alignment.Center,
    ) {
        val spec = with(LocalDensity.current) {
            LatticeSpec(CellWidth.roundToPx(), CellHeight.roundToPx(), Gap.roundToPx())
        }
        val columns = spec.columnsToCover(constraints.maxWidth)
        val count = spec.countFor(spec.rowsToCover(constraints.maxHeight), columns)

        DiamondLattice(
            items = List(count) { index -> covers[index % covers.size] },
            cellWidth = CellWidth,
            cellHeight = CellHeight,
            gap = Gap,
            columns = columns,
        ) { cover ->
            Image(
                painter = painterResource(cover),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/** Public domain first-edition covers; sources are listed in the README. */
private val covers: List<DrawableResource> = listOf(
    Res.drawable.book_hound,
    Res.drawable.book_jungle_book,
    Res.drawable.book_dracula,
    Res.drawable.book_tom_sawyer,
    Res.drawable.book_anne,
    Res.drawable.book_land_of_oz,
    Res.drawable.book_wizard_of_oz,
    Res.drawable.book_sherlock,
    Res.drawable.book_invisible_man,
    Res.drawable.book_black_beauty,
    Res.drawable.book_peter_pan,
    Res.drawable.book_thirty_nine_steps,
    Res.drawable.book_alice,
    Res.drawable.book_war_of_the_worlds,
    Res.drawable.book_secret_garden,
    Res.drawable.book_peter_rabbit,
    Res.drawable.book_huck_finn,
    Res.drawable.book_time_machine,
    Res.drawable.book_rebecca,
    Res.drawable.book_eighty_days,
    Res.drawable.book_wind_willows,
)
