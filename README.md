# Compose Experiments

UI experiments built once with Compose Multiplatform and running on Android, iOS and web.

<a href="https://play.google.com/store/apps/details?id=com.pixel.composeexperiments"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" height="80"></a>

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

Compose Multiplatform `1.12.1`, Kotlin `2.4.20`, AGP `9.3.1`, Gradle `9.5.0`, `compileSdk 37`.

## License

Code is released under the [MIT License](LICENSE).

The book wall uses first-edition covers that are in the public domain, sourced from Wikimedia Commons:

- [Adventures of Huckleberry Finn (1885)](https://commons.wikimedia.org/wiki/File:Adventures_of_Huckleberry_Finn_1885-Cover.png)
- [The Adventures of Sherlock Holmes (1892)](https://commons.wikimedia.org/wiki/File:Adventures_of_sherlock_holmes.jpg)
- [The Adventures of Tom Sawyer (1876)](https://commons.wikimedia.org/wiki/File:Adventures_of_Tom_Sawyer-front_cover.png)
- [Alice's Adventures in Wonderland (1865)](https://commons.wikimedia.org/wiki/File:Alice%27s_Adventures_in_Wonderland_cover_%281865%29.jpg)
- [Anne of Green Gables (1908)](https://commons.wikimedia.org/wiki/File:Anne_of_Green_Gables_-_cover.png)
- [Black Beauty (1877)](https://commons.wikimedia.org/wiki/File:BlackBeautyCoverFirstEd1877.jpeg)
- [Dracula (1897)](https://commons.wikimedia.org/wiki/File:Dracula-First-Edition-1897_%28cropped%29.jpg)
- [The Hound of the Baskervilles (1902)](https://commons.wikimedia.org/wiki/File:Cover_%28Hound_of_Baskervilles%2C_1902%29.jpg)
- [The Invisible Man (1897)](https://commons.wikimedia.org/wiki/File:Wells_-_The_Invisible_Man_-_Pearson_cover_1897.jpg)
- [The Jungle Book (1894)](https://commons.wikimedia.org/wiki/File:The_Jungle_Book_%281894%29_cover.jpg)
- [The Marvelous Land of Oz (1904)](https://commons.wikimedia.org/wiki/File:Marvelous_land_of_oz.jpg)
- [Peter and Wendy (1911)](https://commons.wikimedia.org/wiki/File:Peter_Pan_Cover_1911_b.JPG)
- [Rebecca of Sunnybrook Farm (1903)](https://commons.wikimedia.org/wiki/File:Rebecca_of_Sunnybrook_Farm_001.png)
- [The Secret Garden (1911)](https://commons.wikimedia.org/wiki/File:Houghton_AC85_B9345_911s_-_Secret_Garden%2C_1911_-_cover.jpg)
- [The Tale of Peter Rabbit (1902)](https://commons.wikimedia.org/wiki/File:Peter_Rabbit_first_edition_1902a.jpg)
- [The Thirty-Nine Steps (1915)](https://commons.wikimedia.org/wiki/File:ThirtyNineSteps.jpg)
- [The Time Machine (1895)](https://commons.wikimedia.org/wiki/File:The_Time_Machine_%28Heinemann_text%29_-_front_cover.jpg)
- [Le Tour du monde en quatre-vingts jours (1874)](https://commons.wikimedia.org/wiki/File:Around_the_World_in_Eighty_Days_-_book_cover_%28139390140%29.jpg)
- [The War of the Worlds (1898)](https://commons.wikimedia.org/wiki/File:The_War_of_the_Worlds_by_H._G._Wells_%28US_book_cover%2C_1898%29.jpg)
- [The Wind in the Willows (1908)](https://commons.wikimedia.org/wiki/File:The_Wind_in_the_Willows_cover.jpg)
- [The Wonderful Wizard of Oz (1900)](https://commons.wikimedia.org/wiki/File:The_Wonderful_Wizard_of_Oz_first_edition_cover.jpg)
