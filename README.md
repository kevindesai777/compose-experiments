# Compose Experiments

UI experiments built once with Compose Multiplatform and running on Android, iOS and web.

**Try it in the browser:** [kevindesai777.github.io/compose-experiments](https://kevindesai777.github.io/compose-experiments/)

## Experiments

All shared code lives in `composeApp/src/commonMain`; every experiment is reachable from the home screen.
`androidApp` and `iosApp` are the thin platform entry points.

| Experiment | File |
|---|---|
| Libby Book Arrangement | `libby/` |
| Severance Text Animation | `HelloMsCobel.kt` |
| Lyft Shadow Button | `LyftButtonShadow.kt` |
| Water Tracker Mesh Gradient | `WaterTracker.kt` |

## Running

| Platform | Command |
|---|---|
| Android | `./gradlew :androidApp:installDebug` |
| iOS | Open `iosApp/iosApp.xcodeproj` in Xcode and run |
| Web | `./gradlew :composeApp:wasmJsBrowserDevelopmentRun` |
| Tests | `./gradlew :composeApp:desktopTest` |

## Toolchain

Compose Multiplatform `1.12.1`, Kotlin `2.3.21`, AGP `9.3.1`, Gradle `9.5.0`, `compileSdk 37`.
