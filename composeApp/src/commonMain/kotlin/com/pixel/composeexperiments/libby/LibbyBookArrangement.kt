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
import composeexperiments.composeapp.generated.resources.book1
import composeexperiments.composeapp.generated.resources.book2
import composeexperiments.composeapp.generated.resources.book3
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

private val bookShelf: List<DrawableResource> = List(28) { index ->
    when (index % 3) {
        0 -> Res.drawable.book1
        1 -> Res.drawable.book2
        else -> Res.drawable.book3
    }
}
