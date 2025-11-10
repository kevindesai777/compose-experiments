# Custom Angle-Based Linear Gradient in Jetpack Compose

## Table of Contents
1. [Why We Needed a Custom Implementation](#why-we-needed-a-custom-implementation)
2. [Original Implementation Issues](#original-implementation-issues)
3. [Evolution: Problems & Solutions](#evolution-problems--solutions)
4. [Current Implementation Deep Dive](#current-implementation-deep-dive)
5. [Future Improvements](#future-improvements)

---

## Why We Needed a Custom Implementation

### Built-in Compose Gradient Limitations

Jetpack Compose provides several built-in gradient options:
- `Brush.horizontalGradient()` - Fixed horizontal direction
- `Brush.verticalGradient()` - Fixed vertical direction
- `Brush.linearGradient(start: Offset, end: Offset)` - Requires manual offset calculations

While `linearGradient()` with `Offset` parameters technically allows arbitrary angles, it has significant drawbacks:

**Problem**: To animate a rotating gradient, you need to:
1. Know the size of the component (not available until composition)
2. Calculate start/end offsets for each angle
3. Recalculate on every size change
4. Handle the math complexity yourself

**Example of the manual approach:**
```kotlin
// What you'd need to do manually for a 45° gradient
val size = /* somehow get size */
val angle = 45f * PI / 180f
val length = sqrt(size.width.pow(2) + size.height.pow(2)) / 2
val start = Offset(
    size.width/2 - length * cos(angle),
    size.height/2 + length * sin(angle)
)
val end = Offset(
    size.width/2 + length * cos(angle),
    size.height/2 - length * sin(angle)
)
Brush.linearGradient(colors, start, end)
```

**Solution**: A custom `ShaderBrush` that:
- Accepts angle in degrees (intuitive)
- Automatically handles size changes
- Simplifies rotation animations
- Matches CSS-like gradient syntax

**Ideal API we wanted:**
```kotlin
val gradientBrush = Brush.linearGradient(
    listOf(Color.Red, Color.Yellow),
    angleInDegrees = floatVal  // Just pass the angle!
)
```

---

## Original Implementation Issues

### The Diagonal-Based Approach

The initial implementation calculated gradient endpoints using the diagonal of the bounding box:

```kotlin
private fun getGradientCoordinates(size: Size): Pair<Offset, Offset> {
    val diagonal = sqrt(size.width.pow(2) + size.height.pow(2))
    val angleBetweenDiagonalAndWidth = acos(size.width / diagonal)
    val angleBetweenDiagonalAndGradientLine =
        if ((normalizedAngle > 90 && normalizedAngle < 180)
            || (normalizedAngle > 270 && normalizedAngle < 360)
        ) {
            PI.toFloat() - angleInRadians - angleBetweenDiagonalAndWidth
        } else {
            angleInRadians - angleBetweenDiagonalAndWidth
        }
    val halfGradientLine = abs(cos(angleBetweenDiagonalAndGradientLine) * diagonal) / 2

    val horizontalOffset = halfGradientLine * cos(angleInRadians)
    val verticalOffset = halfGradientLine * sin(angleInRadians)

    val start = size.center + Offset(-horizontalOffset, verticalOffset)
    val end = size.center + Offset(horizontalOffset, -verticalOffset)

    return start to end
}
```

### The Non-Uniform Rotation Problem

**Issue**: When animating from 0° to 360°, the gradient appeared to move at different speeds horizontally vs vertically.

**Root Cause**: The gradient line length varied with angle!

For a rectangular shape (e.g., 400px × 96px button):

```
At 0° (vertical):
┌─────────────────────────────────────┐
│          ↑   96px   ↓               │  Gradient line = 96px (height)
│          │gradient  │               │
└─────────────────────────────────────┘
         ← 400px wide →

At 90° (horizontal):
┌─────────────────────────────────────┐
│                                     │  Gradient line = 400px (width)
│  ← gradient (400px) →               │
└─────────────────────────────────────┘

At 45° (diagonal):
┌─────────────────────────────────────┐
│  ↖                                  │  Gradient line = 412px (diagonal)
│      gradient                       │
│              ↘                      │
└─────────────────────────────────────┘
```

**Mathematical Explanation**:

The gradient line length at any angle θ for a rectangle with width `w` and height `h`:

```
length(θ) = |w/cos(θ)|  if |tan(θ)| < h/w  (intersects left/right edges)
length(θ) = |h/sin(θ)|  if |tan(θ)| > h/w  (intersects top/bottom edges)
```

For our 400×96 button:
- At 0°: length = 96px (shortest)
- At 45°: length ≈ 412px
- At 90°: length = 400px (longest)
- At 135°: length ≈ 412px
- At 180°: length = 96px (shortest again)

**Why This Caused Non-Uniform Speed**:

The gradient transitions from start color to end color over this varying distance. When the distance is short (96px), the transition happens quickly across the screen. When it's long (400px), the transition appears slower.

**Visual Representation of the Problem**:
```
0°-45°: Gradient line grows from 96px → 412px
        Rotation appears SLOW (gradient stretching)

45°-90°: Gradient line shrinks from 412px → 400px
         Rotation appears FASTER (gradient compressing)

90°-135°: Gradient line grows from 400px → 412px
          Rotation appears SLOW again

Pattern repeats every 90°
```

---

## Evolution: Problems & Solutions

### Iteration 1: Constant Radius (Diagonal)

**Attempt**: Use the diagonal as a constant radius

```kotlin
private fun getGradientCoordinates(size: Size): Pair<Offset, Offset> {
    val radius = sqrt(size.width.pow(2) + size.height.pow(2)) / 2
    val horizontalOffset = radius * cos(angleInRadians)
    val verticalOffset = radius * sin(angleInRadians)

    val start = size.center + Offset(-horizontalOffset, verticalOffset)
    val end = size.center + Offset(horizontalOffset, -verticalOffset)

    return start to end
}
```

**Result**: ✅ Uniform rotation speed achieved!

**New Problem**: The gradient line extended way beyond the visible shape, causing color distribution issues.

**Example** (400×96 button):
- Gradient line length = 412px (diagonal)
- But button is only 96px tall
- When gradient is vertical, most colors fall outside visible area
- You'd see only a small portion of red, missing yellow entirely

```
Diagonal approach at 0° (vertical):

     ↑ Red (outside)
     │
     │ Red (outside)
┌────┼────────────────┐ ← Top of button
│    │ Red (visible)  │
│    │                │
│    │ Red→Yellow     │
│    │                │
│    │ Yellow         │
└────┼────────────────┘ ← Bottom of button
     │ Yellow (outside)
     │
     ↓ Yellow (outside)
```

---

### Iteration 2: Maximum Dimension

**Attempt**: Use the larger dimension as radius

```kotlin
val maxDimension = maxOf(size.width, size.height)
val radius = maxDimension
```

**Result**: Still extended too far for our 400×96 button
- radius = 400px
- When vertical, gradient extends 400px above and below center
- Only ~24% of gradient visible in the 96px height

---

### Iteration 3: Minimum Dimension (Current)

**Attempt**: Use the smaller dimension as radius

```kotlin
val minDimension = minOf(size.width, size.height)
val radius = minDimension / 2
```

**Result**:
- ✅ Gradient transition line passes through center
- ✅ Rotation speed is uniform
- ⚠️ But doesn't cover full shape at all angles

**Trade-off**: For 400×96 button (radius = 48px):

```
At 0° (vertical):                At 90° (horizontal):
┌───────────────────────┐        ┌───────────────────────┐
│   ↑ 48px radius ↓     │        │  Yellow (outside)     │
│   │             │     │        │                       │
│   │ gradient    │     │        │  ← 48px → (too short!)│
│   │             │     │        │     Red|Yellow        │
│   Red → Yellow        │        │                       │
│                       │        │  Yellow (outside)     │
└───────────────────────┘        └───────────────────────┘
Works well! Covers height.       Doesn't reach edges!
```

**Current Limitation**: On wide shapes, the gradient doesn't extend to the sides when horizontal/near-horizontal.

---

## Current Implementation Deep Dive

### Full Code Walkthrough

```kotlin
@Immutable
class LinearGradient constructor(
    private val colors: List<Color>,
    private val stops: List<Float>? = null,
    private val tileMode: TileMode = TileMode.Clamp,
    angleInDegrees: Float = 0f,
    useAsCssAngle: Boolean = false
) : ShaderBrush() {

    // Normalize angle to 0-360 range
    private val normalizedAngle: Float = if (useAsCssAngle) {
        ((90 - angleInDegrees) % 360 + 360) % 360  // CSS: 0° = up
    } else {
        (angleInDegrees % 360 + 360) % 360          // Math: 0° = right
    }

    // Convert to radians for trig functions
    private val angleInRadians: Float = (normalizedAngle.toDouble() / 180.0 * PI).toFloat()

    override fun createShader(size: Size): Shader {
        val (from, to) = getGradientCoordinates(size = size)

        return LinearGradientShader(
            colors = colors,
            colorStops = stops,
            from = from,
            to = to,
            tileMode = tileMode
        )
    }

    private fun getGradientCoordinates(size: Size): Pair<Offset, Offset> {
        // Use minimum dimension to keep transition centered
        val minDimension = minOf(size.width, size.height)
        val radius = minDimension / 2

        // Calculate offsets using trigonometry
        val horizontalOffset = radius * cos(angleInRadians)
        val verticalOffset = radius * sin(angleInRadians)

        // Create line through center
        val start = size.center + Offset(-horizontalOffset, verticalOffset)
        val end = size.center + Offset(horizontalOffset, -verticalOffset)

        return start to end
    }
}
```

### Step-by-Step Math Explanation

#### Example: 400×96 Button at Various Angles

**Given**:
- Width = 400px
- Height = 96px
- Center = (200, 48)
- `minDimension = min(400, 96) = 96`
- `radius = 96 / 2 = 48`

#### At 0° (Vertical, pointing right in standard math)

```
angleInRadians = 0
cos(0) = 1
sin(0) = 0

horizontalOffset = 48 * 1 = 48
verticalOffset = 48 * 0 = 0

start = (200, 48) + (-48, 0) = (152, 48)
end = (200, 48) + (48, 0) = (248, 48)

Result: Horizontal gradient from (152,48) to (248,48)
```

```
┌───────────────────────────────────────┐
│                                       │
│          start(152,48) → end(248,48)  │  ← Gradient line (96px)
│                                       │
└───────────────────────────────────────┘
```

#### At 90° (Vertical, pointing up)

```
angleInRadians = π/2
cos(π/2) = 0
sin(π/2) = 1

horizontalOffset = 48 * 0 = 0
verticalOffset = 48 * 1 = 48

start = (200, 48) + (0, -48) = (200, 0)
end = (200, 48) + (0, 48) = (200, 96)

Result: Vertical gradient from (200,0) to (200,96)
```

```
┌───────────────────────────────────────┐
│              ↑ start(200,0)           │
│              │                        │
│              │ gradient (96px)        │
│              │                        │
│              ↓ end(200,96)            │
└───────────────────────────────────────┘
```

#### At 45° (Diagonal)

```
angleInRadians = π/4
cos(π/4) ≈ 0.707
sin(π/4) ≈ 0.707

horizontalOffset = 48 * 0.707 ≈ 34
verticalOffset = 48 * 0.707 ≈ 34

start = (200, 48) + (-34, -34) = (166, 14)
end = (200, 48) + (34, 34) = (234, 82)

Result: Diagonal gradient from (166,14) to (234,82)
Gradient line length = sqrt(68² + 68²) ≈ 96px
```

```
┌───────────────────────────────────────┐
│    ↖ start(166,14)                    │
│        ╲                              │
│          ╲  gradient (96px)           │
│            ╲                          │
│              ↘ end(234,82)            │
└───────────────────────────────────────┘
```

### Key Properties

1. **Gradient line length is constant**: Always 96px (2 × radius)
2. **Always passes through center**: (200, 48)
3. **Uniform rotation speed**: Line rotates like a clock hand
4. **Coordinate system**:
   - 0° points right (East)
   - 90° points down (South) - note: Y increases downward in screen coordinates
   - 180° points left (West)
   - 270° points up (North)

### Color Stop Behavior

With color stops `[0f, 0.35f, 0.65f, 1f]` and colors `[Green, Green, Yellow, Yellow]`:

```
Gradient line (96px):
├──────────┼─────────┼──────────┤
0px        33.6px    62.4px     96px
Green      Green     Yellow     Yellow
           ↑────────↑
           Transition zone (28.8px)
```

- 0% to 35% (0-33.6px): Solid Green
- 35% to 65% (33.6-62.4px): Green → Yellow transition (28.8px blend)
- 65% to 100% (62.4-96px): Solid Yellow

The **center point (48px)** is right in the middle of the transition zone, which is why the division line appears centered on the text.

---

## Future Improvements

### 1. Dynamic Color Stop Adjustment

**Goal**: Make gradient work for all aspect ratios by adjusting color stops based on visible gradient portion.

**Concept**:
```kotlin
private fun adjustedColorStops(size: Size, angle: Float): List<Float> {
    // 1. Calculate full gradient line (using diagonal)
    val fullRadius = sqrt(size.width.pow(2) + size.height.pow(2)) / 2

    // 2. Calculate intersection with rectangle bounds
    val visibleLength = calculateIntersectionLength(size, angle)

    // 3. Determine what portion of gradient is visible
    val visibleRatio = visibleLength / (fullRadius * 2)

    // 4. Adjust stops to center transition in visible area
    val offset = (1 - visibleRatio) / 2
    return originalStops.map { stop ->
        offset + (stop * visibleRatio)
    }
}
```

**Example**: For 400×96 button at 90° (horizontal)
- Full gradient line: 412px (diagonal)
- Visible portion: 400px (width)
- Original stops: [0f, 0.35f, 0.65f, 1f]
- Adjusted stops: [0.015f, 0.357f, 0.643f, 0.985f]
  - Shifts gradient to center the transition

### 2. Intersection Calculation

Calculate exact intersection points with rectangle edges:

```kotlin
private fun calculateIntersectionLength(size: Size, angleInRadians: Float): Float {
    val w = size.width / 2
    val h = size.height / 2

    val abscos = abs(cos(angleInRadians))
    val abssin = abs(sin(angleInRadians))

    // Determine which edges the line intersects
    val intersectsHorizontalEdge = abssin * w > abscos * h

    return if (intersectsHorizontalEdge) {
        // Intersects top and bottom
        2 * h / abssin
    } else {
        // Intersects left and right
        2 * w / abscos
    }
}
```

### 3. Performance Optimizations

**Current**: Recalculates on every composition when size/angle changes

**Improvements**:
- Cache calculations when size doesn't change
- Pre-compute trigonometric values for common angles
- Use `remember` with size/angle as keys

```kotlin
@Composable
fun optimizedGradient(angle: Float) {
    val gradient = remember(angle) {
        Brush.linearGradient(
            colors = colors,
            angleInDegrees = angle
        )
    }
}
```

### 4. Enhanced API

Add convenience parameters:

```kotlin
class LinearGradient(
    colors: List<Color>,
    stops: List<Float>? = null,
    angleInDegrees: Float = 0f,
    useAsCssAngle: Boolean = false,

    // New parameters
    centerTransition: Boolean = false,  // Auto-adjust stops to center
    gradientMode: GradientMode = GradientMode.COVER,  // COVER, CONTAIN, EXACT
    tileMode: TileMode = TileMode.Clamp
)

enum class GradientMode {
    COVER,    // Extend to cover entire shape (diagonal)
    CONTAIN,  // Fit within shape (minDimension)
    EXACT     // Match shape edges at angle
}
```

### 5. Visual Debugging Mode

Add debug overlay to visualize gradient line:

```kotlin
class LinearGradient(
    // ... existing params
    debugMode: Boolean = false  // Show gradient line and intersection points
)
```

### 6. Support for Conic/Radial Variants

Extend pattern to other gradient types:

```kotlin
Brush.Companion.conicGradient(
    colors: List<Color>,
    centerOffset: Offset = Offset.Center,
    startAngle: Float = 0f
)

Brush.Companion.radialGradient(
    colors: List<Color>,
    centerOffset: Offset = Offset.Center,
    radiusMode: RadiusMode = RadiusMode.DIAGONAL
)
```

---

## Conclusion

This custom `LinearGradient` implementation demonstrates the evolution from a simple problem ("I want to specify angle") to a deep understanding of coordinate systems, trigonometry, and shader programming in Compose.

**Current State**:
- ✅ Simple angle-based API
- ✅ Uniform rotation speed
- ✅ Centered transition line
- ⚠️ Limited to shapes where min dimension is acceptable

**Future Work**:
- Implement dynamic color stop adjustment
- Support all aspect ratios perfectly
- Add performance optimizations
- Create comprehensive test suite

The journey highlights an important lesson in graphics programming: **what seems simple (rotating a gradient) often reveals complex mathematical relationships between coordinate systems, visible bounds, and user perception**.
