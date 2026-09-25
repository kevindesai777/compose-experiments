package com.pixel.composeexperiments.libby

import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal val ROOT_2 = sqrt(2f)

data class Placement(
    val x: Int,
    val y: Int,
    val row: Int,
    val col: Int,
    val rotation: Float,
)

/**
 * The math behind the Libby arrangement, a herringbone: cells tilted 45 degrees,
 * one way on even rows and the other way on odd rows.
 *
 * Plain Kotlin with no Compose, so it can be unit tested. Sizes are in pixels,
 * and x and y are always the center of a cell.
 */
class LatticeSpec(
    cellWidth: Int,
    cellHeight: Int,
    gap: Int,
) {
    /** Side of the square box around a tilted cell. */
    val diamond: Float = (cellWidth + cellHeight) / ROOT_2

    /** How far odd rows move right. */
    val rowShift: Float = (cellHeight + gap) / ROOT_2

    /** Distance between two cells in the same row. */
    val pitch: Float = 2 * rowShift

    /** How far each row sits below the one above. */
    val rowPitch: Float = (cellWidth + gap) / ROOT_2

    /** The first cell needs a whole box, and each one after it adds a pitch. */
    fun columnsFor(availableWidth: Int): Int =
        max(1, ((availableWidth - diamond) / pitch).toInt() + 1)

    /**
     * Columns for a field centred in [width] that runs off both sides. The cell
     * just past each edge is missing, so the overflow on each side must be at
     * least `diamond - rowShift` to keep that hole out of view.
     */
    fun columnsToCover(width: Int): Int = max(1, ceil((width + diamond) / pitch).toInt())

    /** Rows for a field centred in [height] that runs off the top and bottom, as above. */
    fun rowsToCover(height: Int): Int = max(1, ceil((height + diamond) / rowPitch).toInt() - 1)

    /** How many items fill [rows] complete rows. */
    fun countFor(rows: Int, columns: Int): Int = rows / 2 * (2 * columns - 1) + rows % 2 * columns

    /**
     * Rows come in pairs: a full row, then one a cell shorter. Dividing the
     * index by the pair size gives its row and column.
     */
    fun placementOf(index: Int, columns: Int): Placement {
        val perPair = 2 * columns - 1
        val pair = index / perPair
        val within = index % perPair

        val evenRow = within < columns
        val row = if (evenRow) 2 * pair else 2 * pair + 1
        val col = if (evenRow) within else within - columns

        val x = diamond / 2 + col * pitch + if (evenRow) 0f else rowShift
        val y = diamond / 2 + row * rowPitch

        return Placement(
            x = x.roundToInt(),
            y = y.roundToInt(),
            row = row,
            col = col,
            rotation = if (evenRow) 45f else -45f,
        )
    }

    fun rowsFor(count: Int, columns: Int): Int =
        if (count <= 0) 0 else placementOf(count - 1, columns).row + 1

    fun fieldWidth(count: Int, columns: Int): Int {
        if (count <= 0) return 0
        return ((min(columns, count) - 1) * pitch + diamond).roundToInt()
    }

    fun fieldHeight(count: Int, columns: Int): Int {
        val rows = rowsFor(count, columns)
        return if (rows == 0) 0 else ((rows - 1) * rowPitch + diamond).roundToInt()
    }
}
