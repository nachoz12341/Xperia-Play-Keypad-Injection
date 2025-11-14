# Xperia-Play-Moga

Minimal Android app for Xperia Play to inject gamepad open/close.

## Requirements
- Java (compatible with the Android Gradle plugin used in the project)
- Android Studio `Android Studio Meerkat Feature Drop | 2024.3.2`
- Gradle wrapper included in the project

## Quick start
1. Clone the repository:
   `git clone https://github.com/nachoz12341/Xperia-Play-Keypad-Injection.git`
2. Open the project in `Android Studio Meerkat Feature Drop | 2024.3.2`
3. Let Android Studio sync and download dependencies (use the Gradle wrapper)
4. Build and install (example):
   `./gradlew assembleDebug` then `adb install -r app/build/outputs/apk/debug/app-debug.apk`

## Project notes
- Build config is managed via `build.gradle` in the project root and module `build.gradle` files.
- Logging has been cleaned to reduce noisy output and provide focused diagnostic messages.
- The UI can remove the title bar for an immersive experience; check the Activity theme or window flags used in the project.

## Contributing
- Fork the repository, make changes, and open a pull request against `main`.
- Keep changes focused (e.g., logging cleanup, bugfix for `dpad_mode`, or UI refinements).

## License
- See the repository `LICENSE` file for details.
