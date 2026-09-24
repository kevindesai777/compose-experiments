# Compose Experiments

UI experiments built once with Compose Multiplatform and running on Android, iOS, desktop and web.

**Try it in the browser:** [kevindesai777.github.io/compose-experiments](https://kevindesai777.github.io/compose-experiments/)

## Experiments

All shared code lives in `composeApp/src/commonMain`; every experiment is reachable from the home screen.

| Experiment | File |
|---|---|
| Libby Book Arrangement | `libby/` |
| Severance Text Animation | `HelloMsCobel.kt` |
| Lyft Shadow Button | `LyftButtonShadow.kt` |
| Water Tracker Mesh Gradient | `WaterTracker.kt` |

## Running

| Platform | Command |
|---|---|
| Android | `./gradlew :composeApp:installDebug` |
| iOS | Open `iosApp/iosApp.xcodeproj` in Xcode and run |
| Desktop | `./gradlew :composeApp:run` |
| Web | `./gradlew :composeApp:wasmJsBrowserDevelopmentRun` |
| Tests | `./gradlew :composeApp:desktopTest` |

## Toolchain

Compose Multiplatform `1.12.1`, Kotlin `2.3.21`, AGP `9.3.1`, Gradle `9.5.0`, `compileSdk 37`.
1.12 is the minimum because the water tracker's `MeshGradientPainter` only renders on
iOS, desktop and web from 1.12.

`gradle.properties` sets `android.builtInKotlin=false` / `android.newDsl=false` so the Android app
plugin can share a module with Kotlin Multiplatform under AGP 9. The long-term fix is splitting out an
[Android app module](https://developer.android.com/kotlin/multiplatform/plugin).
