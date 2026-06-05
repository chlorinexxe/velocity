# Velocity

> A high-fidelity minimalist real-time speed and atmospheric visualizer engine designed for sensory fluidity.

[![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Language](https://img.shields.io/badge/language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Build](https://img.shields.io/badge/build-Gradle-02303A?logo=gradle&logoColor=white)](https://gradle.org)
[![CI](https://img.shields.io/badge/CI-GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)](https://github.com/chlorinexxe/velocity/actions)

---

## About

**Velocity** is an Android application that delivers real-time speed and altitude visualization with a focus on sensory fluidity and minimalist aesthetics. It uses a server-side AI API to power its core engine and is fully written in Kotlin.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin (100%) |
| Platform | Android |
| Build System | Gradle (Kotlin DSL) |
| CI/CD | GitHub Actions |

---

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable)

### Run Locally

1. **Clone the repository**
   ```bash
   git clone https://github.com/chlorinexxe/velocity.git
   cd velocity
   ```

2. **Open in Android Studio**
   Select **File → Open** and choose the project directory. Allow Android Studio to resolve any import incompatibilities automatically.


3. **Fix signing config**
   In `app/build.gradle.kts`, remove the following line before running:
   ```kotlin
   signingConfig = signingConfigs.getByName("debugConfig")
   ```

5. **Run the app**
   Launch on an emulator or a connected physical device via the **Run** button in Android Studio.

---

## Project Structure

```
velocity/
├── app/                    # Main Android application module
├── assets/                 # Project assets
├── gradle/                 # Gradle wrapper files
├── .env.example            # Environment variable template
├── build.gradle.kts        # Root build configuration
├── settings.gradle.kts     # Project settings
└── metadata.json           # App metadata
```

---

## Releases

| Version | Notes |
|---------|-------|
| [1.04](https://github.com/chlorinexxe/velocity/releases/tag/1.04) | Landscape fixes (latest) |
| 1.03 | Initial release |
