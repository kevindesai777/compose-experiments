package com.pixel.composeexperiments.libby

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

/**
 * The cell is the covers' own 2:3 shape, so each cover fills it exactly — no
 * crop, no mat. Proportions are taken from Libby's site: the channel between
 * covers is a fifth of a cover's width, and 28 books make five rows of
 * 6-5-6-5-6 on a wide screen.
 */
@Composable
fun LibbyBookArrangement() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clipToBounds(),
        contentAlignment = Alignment.Center,
    ) {
        DiamondLattice(
            items = bookShelf,
            cellWidth = 84.dp,
            cellHeight = 126.dp,
            gap = 17.dp,
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

private val bookShelf: List<DrawableResource> = List(28) { index -> covers[index % covers.size] }
