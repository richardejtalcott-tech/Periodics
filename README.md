# Periodic 3.0

A fresh Android Studio project for the **Periodic** interactive science app.

## Included in this foundation

- Animated oxygen-atom launch screen
- Dark red/blue/black science-lab visual style
- Complete 118-element interactive periodic table
- Landscape table with drag and pinch-to-zoom
- Tap any element to open an animated atomic shell view
- Java 17 enforcement
- Pinned Android Gradle Plugin 8.8.2 and Gradle 8.10.2
- GitHub Actions workflow that builds and uploads a debug APK
- Codespaces devcontainer configuration

This is a stable working foundation for the larger roadmap. The advanced true-3D models, isotope lab, ion builder, bond builder, quark explorer, full scientific datasets, search and favorites are future feature modules rather than claims of being finished in this first clean build.

## Build in GitHub

Upload the contents of this folder to the repository root and push to `main`. Open **Actions → Build Android APK**. After the workflow finishes, download the `Periodic-v3-debug-apk` artifact.

## Build in Android Studio

Open this folder as a project. Use JDK 17 and install Android SDK Platform 35. Android Studio may use its bundled Gradle integration, or run:

```bash
chmod +x gradlew
./gradlew clean assembleDebug
```

APK output:

`app/build/outputs/apk/debug/app-debug.apk`

## Technical choices

The app intentionally uses Android framework classes and custom Canvas rendering with no runtime libraries. This minimizes dependency failures and provides a clean base for later 3D integration.
