# Compose Experiments

UI experiments built with Compose Multiplatform, shipped as an Android app and a web app
(GitHub Pages). An iOS app builds from the same code but isn't on the App Store yet.

## Layout

- `composeApp/` — Kotlin Multiplatform library (`com.android.kotlin.multiplatform.library`) with
  all shared UI and every experiment in `commonMain`. Also builds the wasm web app and the iOS
  framework. Its name is load-bearing: the generated `Res` package and the Xcode build phase
  (`:composeApp:embedAndSignAppleFrameworkForXcode`) depend on it.
- `androidApp/` — thin Android entry point: `MainActivity`, manifest, adaptive icon, signing, R8.
  Icon SVG sources live in `androidApp/icon/`; the Play Store icon is
  `androidApp/src/main/ic_launcher-playstore.png`.
- `iosApp/` — Xcode project.

## Commands

- Android debug install: `./gradlew :androidApp:installDebug`
- Android release bundle: `./gradlew :androidApp:bundleRelease -PversionCode=N`
- Web dev server: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`
- Tests: `./gradlew :composeApp:desktopTest` (the desktop target exists only for tests)

## Conventions

- Commits are authored as `Kevin Desai <kevindesai777@gmail.com>`. No Claude/AI attribution
  in commit messages, PR descriptions or co-author trailers.
- PRs are merged with merge commits.
- Every screen is dark and `MainActivity` uses light system-bar icons; inset content with
  `safeDrawingPadding()` while backgrounds stay full-bleed.
- Home screen, icon, privacy page and store graphics share one look: navy `#0B2E42` to teal
  `#0E5049` diagonal with a faint `#3DDC84` grid.

## Release

- Signing: gitignored `keystore.properties` locally, or the `ANDROID_KEYSTORE_BASE64`,
  `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD` secrets in CI. The
  *Android Release Bundle* workflow sets `versionCode` to the run number.
- Privacy policy: `composeApp/src/wasmJsMain/resources/privacy.html`, served at
  https://kevindesai777.github.io/compose-experiments/privacy.html. It states the app collects
  no data and requests no permissions (not even internet); update it before any release that
  changes that.
- Keep third-party brand names (Libby, Lyft, Severance) out of store listing text.

## Store listing

Play Store text, feature graphic, screenshots and the graphic's HTML source live in `store/`
(see `store/listing.md`).

## TODO: when the iOS app is on the App Store

- Swap the Play feature graphic for `store/feature-graphic-with-ios.png` (chips Android · iOS · Web).
- Mention iOS again in the Play short description ("…for Android, iOS and the web") and the
  first paragraph of the full description, and update `store/listing.md` to match.
