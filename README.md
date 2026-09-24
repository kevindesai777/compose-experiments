This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop.

## Experiments

Every experiment lives in `composeApp/src/commonMain` and is reachable from the home screen.

| Experiment | File |
|---|---|
| Libby Book Arrangement | `libby/` |
| Severance Text Animation | `HelloMsCobel.kt` |
| Lyft Shadow Button | `LyftButtonShadow.kt` |
| Water Tracker Mesh Gradient | `WaterTracker.kt` |

The Libby layout's geometry is unit tested in `composeApp/src/commonTest`; run it with
`./gradlew :composeApp:desktopTest`.

### A note on the toolchain

The water tracker draws its surface with `MeshGradientPainter`, and Compose Multiplatform
only started rendering that on the Skiko backends (iOS, desktop, web) in **1.12.0-beta01**.
That sets the floor for the whole repo:

* Compose Multiplatform `1.12.1` (stable), with Navigation `2.10.0-beta01` and Lifecycle `2.11.0`
  as JetBrains pairs them for that release
* Kotlin `2.3.21` — CMP 1.12 needs 2.3.20+ for the Kotlin/Wasm target
* AGP `9.3.1` + Gradle `9.5.0` + `compileSdk 37` — Compose 1.12's Android artifacts
  refuse to resolve below AGP 9.1 / API 37

AGP 9 also stopped allowing `com.android.application` in the same module as the Kotlin
Multiplatform plugin, so `gradle.properties` carries AGP's documented `android.builtInKotlin=false` /
`android.newDsl=false` bypass. The durable fix is splitting `composeApp` into a
[`com.android.kotlin.multiplatform.library`](https://developer.android.com/kotlin/multiplatform/plugin)
module plus a thin Android app module.

### Releasing to Google Play

Release builds are minified with R8 (`composeApp/proguard-rules.pro`) and signed with an
upload key for [Play App Signing](https://support.google.com/googleplay/android-developer/answer/9842756).

1. Create the upload key once and keep it somewhere safe (it is gitignored):
   ```
   keytool -genkeypair -v -keystore upload.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
   ```
2. **Local build**: add a `keystore.properties` at the repo root:
   ```
   storeFile=upload.jks
   storePassword=…
   keyAlias=upload
   keyPassword=…
   ```
   then run `./gradlew :composeApp:bundleRelease -PversionCode=2`. The bundle lands in
   `composeApp/build/outputs/bundle/release/`.
3. **CI build**: add the repository secrets `ANDROID_KEYSTORE_BASE64` (`base64 -w0 upload.jks`),
   `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS` and `ANDROID_KEY_PASSWORD`. The
   *Android Release Bundle* workflow then builds a signed `.aab` on every PR, `v*` tag or manual
   run, with `versionCode` set to the run number. Download it and the R8 `mapping.txt` from the
   run's artifacts. Upload the mapping file alongside the bundle so Play Console crash reports are deobfuscated.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [GitHub](https://github.com/JetBrains/compose-multiplatform/issues).

You can open the web application by running the `:composeApp:wasmJsBrowserDevelopmentRun` Gradle task.

The web application has been deployed on github pages and you can check it out here:
[Compose Multiplatform on Web](https://kevindesai777.github.io/compose-experiments/)