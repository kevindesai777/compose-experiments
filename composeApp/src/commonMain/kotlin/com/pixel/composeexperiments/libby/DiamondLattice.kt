package com.pixel.composeexperiments.libby

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import kotlin.math.max

/**
 * Lays items out like the book covers in the Libby app. [gap] is the space
 * between two covers. Items are drawn upright; the layout does the tilting.
 */
@Composable
fun <T> DiamondLattice(
    items: List<T>,
    cellWidth: Dp,
    cellHeight: Dp,
    gap: Dp,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit,
) {
    Layout(
        modifier = modifier,
        content = { items.forEach { itemContent(it) } },
    ) { measurables, constraints ->
        val cellWidthPx = cellWidth.roundToPx()
        val cellHeightPx = cellHeight.roundToPx()
        val spec = LatticeSpec(cellWidthPx, cellHeightPx, gap.roundToPx())

        // In a horizontal scroll there is no width limit, so use one long row.
        val columns =
            if (constraints.hasBoundedWidth) spec.columnsFor(constraints.maxWidth)
            else max(1, measurables.size)

        // Every item gets exactly the cell size, so they all fit the pattern.
        val cell = Constraints.fixed(cellWidthPx, cellHeightPx)
        val placeables = measurables.map { it.measure(cell) }

        layout(
            width = spec.fieldWidth(placeables.size, columns),
            height = spec.fieldHeight(placeables.size, columns),
        ) {
            placeables.forEachIndexed { index, placeable ->
                val placement = spec.placementOf(index, columns)
                // place() wants the top-left corner, and we have the center.
                placeable.placeWithLayer(
                    x = placement.x - placeable.width / 2,
                    y = placement.y - placeable.height / 2,
                ) { rotationZ = placement.rotation }
            }
        }
    }
}
