# NOCTURNE 2.0 – Anime Tic-Tac-Toe

**Application ID / package:** `com.kamlendra.tictactoe`  
**Version:** 2.0  
**Language:** Kotlin  
**UI:** Jetpack Compose  
**Minimum Android:** 7.0 (API 24)  
**Compile SDK:** 36  
**Target SDK:** 35

## Included in this project
- Premium dark burgundy / rose-gold / lavender Compose interface.
- Three original stylized in-app character illustrations: Reina (24), Miyuki (23), Aiko (22).
- Character selection and discreet alternate-style setting (OFF by default).
- Player vs AI and local two-player modes.
- Exhaustive Minimax AI. With optimal play on both sides, Tic-Tac-Toe cannot be forced into a loss for the AI.
- Win, loss, draw feedback, animated confetti particles, cinematic loss streaks, draw ripple and a glowing winning line.
- Animated screen transitions, portraits, result banner, board marks, winning line and custom launcher icon.
- Local scoreboard and preference persistence.
- Original generated ambient WAV loop and on-device move/result tone effects.
- Music pauses when the app goes into the background.
- Unit tests for game rules and AI move selection.

## Project origin
The supplied earlier ZIP contained Gradle/configuration and a compiled APK, but did not include the app's `src/main` source tree or a complete Gradle wrapper distribution. This project is therefore a clean reconstruction using the same application ID/package, not a byte-for-byte upgrade of missing source files.

## Open the project
1. Extract `NOCTURNE_2_0_Project.zip` to a normal folder.
2. Open Android Studio.
3. Select **Open** and choose the extracted `NOCTURNE_2_0` folder (the folder containing `settings.gradle.kts`).
4. Use Android Studio's bundled JDK 17: **Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**.
5. Install Android SDK Platform 36 if Android Studio asks for it.
6. Allow Gradle sync to complete. Android Studio will resolve the plugin and library versions from the declared repositories.
7. Connect an Android phone with USB debugging enabled, or start an API 24+ emulator.
8. Press **Run ▶**.

## Generate a debug APK
Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.

The output is normally:
`app/build/outputs/apk/debug/app-debug.apk`

To run unit tests from Android Studio:
`app/src/test/java/com/kamlendra/tictactoe/GameLogicTest.kt`

## Audio
`app/src/main/res/raw/nocturne_ambient.wav` is an original synthesized ambient sound loop. Sound effects use Android's local `ToneGenerator`, so no internet or permissions are required.

## Artwork
The portraits are original stylized vector illustrations drawn in Kotlin Canvas at runtime. They are not imported screenshots or copyrighted anime characters. For a later art pass, replace `CharacterArt.kt` with commissioned / user-owned character images while retaining the same character model.

## Build verification status
This workspace did not have an Android SDK, Android Gradle Plugin installation, or Gradle executable available. The ZIP structure and source files were generated, but a real Android Gradle build/APK compile could not be executed here. Open in Android Studio and let it sync/build; if it reports an error, share the exact Build Output or screenshot for a targeted correction.


## Build verification

The workspace did not have an Android SDK, Android Gradle Plugin installation, or Gradle executable available. The ZIP structure, XML resources and archive integrity were checked, but a real Android Gradle build/APK compile could not be executed here. The input ZIP did not contain a Gradle Wrapper JAR, so this reconstruction does not pretend to include a runnable wrapper. Open with Android Studio, use JDK 17 and Gradle 8.13 for the AGP 8.13.2 project, and let it resolve dependencies; if sync reports an error, share the exact Build Output for a targeted correction.
