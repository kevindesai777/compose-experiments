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

## Releasing to Google Play

Release builds are minified with R8 (`composeApp/proguard-rules.pro`) and signed with an
upload key for [Play App Signing](https://support.google.com/googleplay/android-developer/answer/9842756).

1. Create the upload key once and keep it safe (`*.jks` is gitignored):
   ```
   keytool -genkeypair -v -keystore upload.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
   ```
2. **Local build:** add `keystore.properties` at the repo root (gitignored):
   ```
   storeFile=upload.jks
   storePassword=…
   keyAlias=upload
   keyPassword=…
   ```
   then run `./gradlew :composeApp:bundleRelease -PversionCode=2`. The bundle lands in
   `composeApp/build/outputs/bundle/release/`.
3. **CI build:** add the repository secrets `ANDROID_KEYSTORE_BASE64` (`base64 -w0 upload.jks`),
   `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS` and `ANDROID_KEY_PASSWORD`. The
   *Android Release Bundle* workflow builds a signed `.aab` on every PR, `v*` tag or manual run,
   with `versionCode` set to the run number. Upload the `mapping.txt` from the run's artifacts
   alongside the bundle so Play Console crash reports are deobfuscated.
