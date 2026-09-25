package com.pixel.composeexperiments.libby

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Centres are rounded to whole pixels, so anything measured between two covers
// can be out by a little over a pixel.
private const val TOLERANCE = 1.5f

class LatticeSpecTest {

    // A 2:3 cover and a 12px channel.
    private val w = 120
    private val h = 180
    private val gap = 12
    private val spec = LatticeSpec(w, h, gap)

    @Test
    fun boundingBoxIsTheSumOfTwoShadows() {
        // 120 / sqrt(2) + 180 / sqrt(2) = 84.9 + 127.3
        assertEquals(212.13f, spec.diamond, 0.01f)
    }

    @Test
    fun rowsSlideByTheLongSideAndStepByTheShortOne() {
        assertEquals(135.76f, spec.rowShift, 0.01f) // (180 + 12) / sqrt(2)
        assertEquals(271.53f, spec.pitch, 0.01f)
        assertEquals(93.34f, spec.rowPitch, 0.01f) // (120 + 12) / sqrt(2)
    }

    @Test
    fun aSquareCellSpacesRowsHalfAPitchApart() {
        val square = LatticeSpec(150, 150, 12)
        assertEquals(square.pitch / 2, square.rowPitch, 0.01f)
    }

    @Test
    fun rowsAlternateWhichWayTheyLean() {
        spec.place(30, columns = 5).forEach {
            assertEquals(if (it.row % 2 == 0) 45f else -45f, it.rotation, "row ${it.row}")
        }
    }

    @Test
    fun noTwoCoversOverlapOrSitCloserThanTheGap() {
        // portrait, square, very tall and landscape cells
        listOf(120 to 180, 100 to 150, 150 to 150, 90 to 160, 160 to 120).forEach { (cw, ch) ->
            val boxes = LatticeSpec(cw, ch, gap).place(30, columns = 5).map { box45(it, cw, ch) }
            for (i in boxes.indices) for (j in i + 1 until boxes.size) {
                val apart = distance(boxes[i], boxes[j])
                assertTrue(apart >= gap - TOLERANCE, "$cw x $ch: covers $i and $j are $apart apart")
            }
        }
    }

    @Test
    fun everyChannelBetweenNeighboursIsExactlyTheGap() {
        val at = spec.place(30, columns = 5).associateBy { it.row to it.col }
        fun between(a: Pair<Int, Int>, b: Pair<Int, Int>) =
            distance(box45(at.getValue(a), w, h), box45(at.getValue(b), w, h))

        // down into the next row, back across a column, and two rows straight down
        assertEquals(gap.toFloat(), between(0 to 0, 1 to 0), TOLERANCE)
        assertEquals(gap.toFloat(), between(0 to 1, 1 to 0), TOLERANCE)
        assertEquals(gap.toFloat(), between(0 to 0, 2 to 0), TOLERANCE)
        assertEquals(gap.toFloat(), between(1 to 0, 3 to 0), TOLERANCE)
    }

    @Test
    fun consecutiveRowsLineUpAlongAnEdge() {
        // The detail that makes the arrangement look deliberate: the first cover
        // of each row shares a straight edge with the first cover of the next.
        // Upper-right edges line up from an even row into an odd one, upper-left
        // edges from an odd row into an even one.
        val at = spec.place(30, columns = 5).associateBy { it.row to it.col }
        fun first(row: Int) = box45(at.getValue(row to 0), w, h)

        for (row in 0 until 5) {
            val here = first(row)
            val next = first(row + 1)
            if (row % 2 == 0) {
                assertEquals(here.vMin, next.vMin, TOLERANCE, "upper-right edges, rows $row and ${row + 1}")
            } else {
                assertEquals(here.uMin, next.uMin, TOLERANCE, "upper-left edges, rows $row and ${row + 1}")
            }
        }
    }

    @Test
    fun oddRowsHoldOneFewerAndStartHalfAPitchIn() {
        val columns = 5
        // 0..4 fill row 0, 5..8 fill row 1, 9 opens row 2.
        assertEquals(0, spec.placementOf(4, columns).row)
        assertEquals(1, spec.placementOf(5, columns).row)
        assertEquals(1, spec.placementOf(8, columns).row)
        assertEquals(2, spec.placementOf(9, columns).row)

        val evenRowStart = spec.placementOf(0, columns).x
        val oddRowStart = spec.placementOf(5, columns).x
        assertEquals(spec.rowShift, (oddRowStart - evenRowStart).toFloat(), TOLERANCE)
    }

    @Test
    fun theCountsThatBrokeTheFirstVersion() {
        // v1 threw IndexOutOfBounds at 12 and 26, and silently dropped books at
        // 25 and 100. Now the count in equals the count out, and no two books
        // share a centre.
        val columns = spec.columnsFor(1400)
        listOf(0, 1, 10, 12, 25, 26, 27, 30, 100).forEach { count ->
            val placed = spec.place(count, columns)
            assertEquals(count, placed.size, "books placed for $count")
            assertEquals(count, placed.map { it.x to it.y }.toSet().size, "shared centres at $count")
        }
    }

    @Test
    fun noBookEscapesTheReportedField() {
        val count = 30
        val columns = spec.columnsFor(1400)
        val width = spec.fieldWidth(count, columns)
        val height = spec.fieldHeight(count, columns)

        spec.place(count, columns).forEachIndexed { index, p ->
            corners(p, w, h).forEach { (x, y) ->
                assertTrue(x >= -TOLERANCE && x <= width + TOLERANCE, "book $index escapes sideways at $x")
                assertTrue(y >= -TOLERANCE && y <= height + TOLERANCE, "book $index escapes vertically at $y")
            }
        }
    }

    @Test
    fun theFieldFillsTheWidthWithoutOverflowing() {
        listOf(300, 480, 600, 884, 1120, 1400, 2400).forEach { available ->
            val columns = spec.columnsFor(available)
            assertTrue(spec.fieldWidth(100, columns) <= available, "overflows $available")
            // and one more column would not have fit
            assertTrue(columns * spec.pitch + spec.diamond > available, "wasted a column at $available")
        }
    }

    @Test
    fun aCoveringFieldLeavesNoHoleAtTheEdges() {
        // Centre the field on the screen and probe it: every point should be on a
        // cover or in a channel beside one, never in a gap a missing cell left.
        val screens = listOf(360 to 640, 412 to 915, 600 to 960, 884 to 600, 1440 to 900, 1731 to 1000)
        listOf(Triple(w, h, gap), Triple(84, 126, 17)).forEach { (cw, ch, g) ->
            val s = LatticeSpec(cw, ch, g)
            screens.forEach { (sw, sh) ->
                val columns = s.columnsToCover(sw)
                val count = s.countFor(s.rowsToCover(sh), columns)
                val dx = (s.fieldWidth(count, columns) - sw) / 2f
                val dy = (s.fieldHeight(count, columns) - sh) / 2f
                assertTrue(dx >= 0 && dy >= 0, "$sw x $sh: field smaller than the screen")

                val boxes = s.place(count, columns).map { box45(it, cw, ch) }
                for (x in 0..sw step 4) for (y in 0..sh step 4) {
                    val u = (x + dx + y + dy) / ROOT_2
                    val v = (y + dy - x - dx) / ROOT_2
                    val nearest = boxes.minOf { hypot(max(0f, max(it.uMin - u, u - it.uMax)), max(0f, max(it.vMin - v, v - it.vMax))) }
                    assertTrue(nearest <= g + TOLERANCE, "$sw x $sh: hole at ($x, $y)")
                }
            }
        }
    }

    @Test
    fun countForFillsWholeRows() {
        val columns = 3
        listOf(1, 2, 5, 12).forEach { rows ->
            val count = spec.countFor(rows, columns)
            assertEquals(rows, spec.rowsFor(count, columns), "rows for $count")
            assertEquals(rows + 1, spec.rowsFor(count + 1, columns), "one more starts row ${rows + 1}")
        }
    }

    @Test
    fun columnsNeverDropBelowOne() {
        assertEquals(1, spec.columnsFor(0))
        assertEquals(1, spec.columnsFor(50))
    }

    @Test
    fun rowCountMatchesTheLastBookRow() {
        val columns = 5
        assertEquals(0, spec.rowsFor(0, columns))
        assertEquals(1, spec.rowsFor(1, columns))
        assertEquals(1, spec.rowsFor(5, columns))
        assertEquals(2, spec.rowsFor(6, columns))
        assertEquals(2, spec.rowsFor(9, columns))
        assertEquals(3, spec.rowsFor(10, columns))
        assertEquals(7, spec.rowsFor(30, columns))
    }

    @Test
    fun aSingleColumnStacksWithoutOverlapping() {
        val placed = spec.place(6, columns = 1)
        assertTrue(placed.all { it.col == 0 && it.rotation == 45f })

        val boxes = placed.map { box45(it, w, h) }
        for (i in 0 until boxes.size - 1) {
            assertTrue(distance(boxes[i], boxes[i + 1]) >= gap - TOLERANCE, "covers $i and ${i + 1}")
        }
    }
}

private fun LatticeSpec.place(count: Int, columns: Int) = (0 until count).map { placementOf(it, columns) }

/** The four corners of a cover in screen space, after it has been tilted. */
private fun corners(p: Placement, w: Int, h: Int): List<Pair<Float, Float>> {
    val radians = p.rotation * PI.toFloat() / 180f
    val c = cos(radians)
    val s = sin(radians)
    return listOf(w / 2f to h / 2f, w / 2f to -h / 2f, -w / 2f to -h / 2f, -w / 2f to h / 2f)
        .map { (a, b) -> (p.x + a * c - b * s) to (p.y + a * s + b * c) }
}

/**
 * A cover's extent in a frame turned 45 degrees, where u runs down-right and v
 * runs down-left. Every cover is an upright rectangle in this frame, which is
 * what makes gaps and edges easy to measure.
 */
private data class Box45(val uMin: Float, val uMax: Float, val vMin: Float, val vMax: Float)

private fun box45(p: Placement, w: Int, h: Int): Box45 {
    val points = corners(p, w, h)
    val us = points.map { (x, y) -> (x + y) / ROOT_2 }
    val vs = points.map { (x, y) -> (y - x) / ROOT_2 }
    return Box45(us.min(), us.max(), vs.min(), vs.max())
}

/** How far apart two covers are; negative when they overlap. */
private fun distance(a: Box45, b: Box45): Float {
    val alongU = max(b.uMin - a.uMax, a.uMin - b.uMax)
    val alongV = max(b.vMin - a.vMax, a.vMin - b.vMax)
    return if (alongU > 0f && alongV > 0f) hypot(alongU, alongV) else max(alongU, alongV)
}
